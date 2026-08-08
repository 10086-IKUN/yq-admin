package cn.yanque.models.studentFront.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.mockinterview.mapper.MockInterviewSessionMapper;
import cn.yanque.models.mockinterview.pojo.entity.MockInterviewSessionEntity;
import cn.yanque.models.interview.pojo.InterviewQuestionBankEntity;
import cn.yanque.models.interview.service.InterviewQuestionService;
import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.client.DoubaoRealtimeVoiceClient;
import cn.yanque.models.studentFront.client.MockInterviewProfileClient;
import cn.yanque.models.studentFront.pojo.dto.MockInterviewProfileGenerateReq;
import cn.yanque.models.studentFront.pojo.dto.MockInterviewProfileGenerateRes;
import cn.yanque.models.studentFront.pojo.req.StudentMockInterviewCreateReq;
import cn.yanque.models.studentFront.pojo.res.StudentMockInterviewProfileRes;
import cn.yanque.models.studentFront.pojo.res.StudentMockInterviewVoiceStartRes;
import cn.yanque.models.studentFront.service.StudentMockInterviewService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class StudentMockInterviewServiceImpl implements StudentMockInterviewService {

    private static final String PARSE_SUCCESS = "SUCCESS";

    private static final String PROFILE_PROCESSING = "PROFILE_PROCESSING";

    private static final String SESSION_STATUS_PROFILE_READY = "PROFILE_READY";

    private static final String PROFILE_SUCCESS = "PROFILE_SUCCESS";

    private static final int RESUME_TEXT_LIMIT = 12000;

    private static final List<String> KNOWN_SKILLS = List.of(
            "Java", "Spring Boot", "Spring Cloud", "MyBatis", "MySQL", "Redis", "Vue", "React",
            "Docker", "Linux", "Nginx", "RabbitMQ", "Kafka", "Elasticsearch", "Git", "Maven"
    );

    @Autowired
    private EduStudentMapper studentMapper;

    @Autowired
    private MockInterviewSessionMapper mockInterviewSessionMapper;

    @Autowired
    private MockInterviewProfileClient mockInterviewProfileClient;

    @Autowired
    private DoubaoRealtimeVoiceClient doubaoRealtimeVoiceClient;

    @Autowired
    private InterviewQuestionService interviewQuestionService;

    @Override
    public StudentMockInterviewProfileRes getProfile(Long studentId) {
        EduStudentEntity student = getStudent(studentId);
        return buildRes(student, mockInterviewSessionMapper.selectLatestByStudentId(studentId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentMockInterviewProfileRes createSession(Long studentId, StudentMockInterviewCreateReq req) {
        EduStudentEntity student = getStudent(studentId);
        if (isBlank(student.getResumeObjectKey())) {
            throw BusinessException.DateError.newInstance("请先上传简历");
        }
        if (!PARSE_SUCCESS.equals(student.getResumeParseStatus()) || isBlank(student.getResumeText())) {
            throw BusinessException.DateError.newInstance("简历还未解析完成");
        }

        Date now = new Date();
        MockInterviewSessionEntity session = new MockInterviewSessionEntity();
        session.setStudentId(studentId);
        session.setResumeObjectKey(student.getResumeObjectKey());
        session.setResumeFileName(student.getResumeFileName());
        session.setResumeTextSnapshot(student.getResumeText());
        session.setProfileStatus(PROFILE_PROCESSING);
        session.setStatus(SESSION_STATUS_PROFILE_READY);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        mockInterviewSessionMapper.insert(session);

        try {
            JSONObject profile = requestAiProfile(student, session);
            mockInterviewSessionMapper.updateProfileSuccess(session.getId(), JSON.toJSONString(profile), new Date());
        } catch (Exception e) {
            JSONObject fallbackProfile = buildFallbackProfile(student, session);
            mockInterviewSessionMapper.updateProfileSuccess(session.getId(), JSON.toJSONString(fallbackProfile), new Date());
        }
        return buildRes(studentMapper.selectById(studentId), mockInterviewSessionMapper.selectById(session.getId()));
    }

    @Override
    public StudentMockInterviewVoiceStartRes startVoice(Long studentId, Long sessionId) {
        getStudent(studentId);
        if (sessionId == null) {
            throw BusinessException.DateError.newInstance("模拟面试会话不能为空");
        }
        MockInterviewSessionEntity session = mockInterviewSessionMapper.selectById(sessionId);
        if (session == null || !studentId.equals(session.getStudentId())) {
            throw BusinessException.DateError.newInstance("模拟面试会话不存在");
        }
        if (!PROFILE_SUCCESS.equals(session.getProfileStatus()) || isBlank(session.getProfileJson())) {
            throw BusinessException.DateError.newInstance("请先生成本次面试画像");
        }
        JSONObject profile = parseProfile(session.getProfileJson());
        if (profile == null || profile.isEmpty()) {
            throw BusinessException.DateError.newInstance("本次面试画像异常，请重新生成");
        }
        if (!SESSION_STATUS_PROFILE_READY.equals(session.getStatus())) {
            throw BusinessException.DateError.newInstance("当前模拟面试会话不可重复开始");
        }

        List<InterviewQuestionBankEntity> recommendedQuestions = interviewQuestionService
                .recommendPublishedForMockInterview(buildQuestionRetrievalQuery(profile), 8);
        StudentMockInterviewVoiceStartRes voiceRes = doubaoRealtimeVoiceClient
                .start(sessionId, buildVoicePrompt(profile, recommendedQuestions));
        Date now = new Date();
        int updated = mockInterviewSessionMapper.updateStarted(sessionId, studentId, voiceRes.getVoiceSessionId(), now);
        if (updated != 1) {
            doubaoRealtimeVoiceClient.close(voiceRes.getVoiceSessionId());
            throw BusinessException.DateError.newInstance("当前模拟面试会话已开始，请勿重复操作");
        }
        return voiceRes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentMockInterviewProfileRes finishVoice(Long studentId, Long sessionId) {
        EduStudentEntity student = getStudent(studentId);
        if (sessionId == null) {
            throw BusinessException.DateError.newInstance("模拟面试会话不能为空");
        }
        MockInterviewSessionEntity session = mockInterviewSessionMapper.selectById(sessionId);
        if (session == null || !studentId.equals(session.getStudentId())) {
            throw BusinessException.DateError.newInstance("模拟面试会话不存在");
        }
        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw BusinessException.DateError.newInstance("当前模拟面试会话未在进行中");
        }

        doubaoRealtimeVoiceClient.close(session.getVoiceSessionId());
        int updated = mockInterviewSessionMapper.updateFinished(sessionId, studentId, new Date());
        if (updated != 1) {
            throw BusinessException.DateError.newInstance("模拟面试会话状态已变更");
        }
        return buildRes(student, mockInterviewSessionMapper.selectById(sessionId));
    }

    private JSONObject requestAiProfile(EduStudentEntity student, MockInterviewSessionEntity session) {
        MockInterviewProfileGenerateReq req = new MockInterviewProfileGenerateReq();
        req.setStudentId(student.getId());
        req.setStudentName(student.getStudentName());
        req.setEducation(student.getEducation());
        req.setGradeYear(student.getGraduationSession());
        req.setSchool(student.getSchool());
        req.setMajor(null);
        req.setResumeText(limitText(session.getResumeTextSnapshot(), RESUME_TEXT_LIMIT));

        MockInterviewProfileGenerateRes res = mockInterviewProfileClient.generate(req);
        if (res == null || res.getProfile() == null || res.getProfile().isEmpty()) {
            throw BusinessException.RemoteError.newInstance("模拟面试画像为空");
        }
        return res.getProfile();
    }

    private JSONObject buildFallbackProfile(EduStudentEntity student, MockInterviewSessionEntity session) {
        String resumeText = session.getResumeTextSnapshot() == null ? "" : session.getResumeTextSnapshot();
        JSONArray skills = new JSONArray();
        KNOWN_SKILLS.stream()
                .filter(skill -> containsIgnoreCase(resumeText, skill))
                .forEach(skills::add);
        if (skills.isEmpty() && containsIgnoreCase(resumeText, "Spring")) {
            skills.add("Spring Boot");
        }

        String targetPosition = inferTargetPosition(resumeText, skills);
        JSONArray questionTags = new JSONArray();
        skills.forEach(questionTags::add);
        questionTags.add("项目经验");
        questionTags.add("简历深挖");

        JSONArray riskPoints = new JSONArray();
        if (skills.isEmpty()) {
            riskPoints.add("简历中可识别的技术关键词较少，建议先确认目标岗位和核心技能。");
        }
        if (containsIgnoreCase(resumeText, "项目") && !containsIgnoreCase(resumeText, "负责")) {
            riskPoints.add("项目经历描述可能偏概括，需要通过追问确认个人职责和技术细节。");
        }

        JSONObject profile = new JSONObject();
        profile.put("targetPosition", targetPosition);
        profile.put("level", inferLevel(student, resumeText));
        profile.put("skills", skills);
        profile.put("projects", new JSONArray());
        profile.put("questionTags", questionTags);
        profile.put("suggestedInterviewType", targetPosition + "实习/初级");
        profile.put("riskPoints", riskPoints);
        profile.put("source", "RULE_FALLBACK");
        return profile;
    }

    private String inferTargetPosition(String resumeText, JSONArray skills) {
        if (containsIgnoreCase(resumeText, "Java") || skills.contains("Spring Boot") || skills.contains("MySQL")) {
            return "Java 后端开发";
        }
        if (skills.contains("Vue") || skills.contains("React")) {
            return "前端开发";
        }
        return "软件开发";
    }

    private String inferLevel(EduStudentEntity student, String resumeText) {
        if (resumeText.contains("实习") || resumeText.contains("应届") || student.getGraduationSession() != null) {
            return "实习/初级";
        }
        return "初级";
    }

    private StudentMockInterviewProfileRes buildRes(EduStudentEntity student, MockInterviewSessionEntity latestSession) {
        StudentMockInterviewProfileRes res = new StudentMockInterviewProfileRes();
        res.setHasResume(!isBlank(student.getResumeObjectKey()));
        res.setResumeFileName(student.getResumeFileName());
        res.setResumeParseStatus(student.getResumeParseStatus());
        res.setResumeParseErrorMessage(student.getResumeParseErrorMessage());
        res.setResumeParsedAt(student.getResumeParsedAt());
        res.setLatestSession(buildSessionRes(latestSession));
        return res;
    }

    private StudentMockInterviewProfileRes.MockInterviewSessionRes buildSessionRes(MockInterviewSessionEntity session) {
        if (session == null) {
            return null;
        }
        StudentMockInterviewProfileRes.MockInterviewSessionRes res = new StudentMockInterviewProfileRes.MockInterviewSessionRes();
        res.setId(session.getId());
        res.setProfileStatus(session.getProfileStatus());
        res.setProfileErrorMessage(session.getProfileErrorMessage());
        res.setProfileGeneratedAt(session.getProfileGeneratedAt());
        res.setStatus(session.getStatus());
        res.setVoiceSessionId(session.getVoiceSessionId());
        res.setStartedAt(session.getStartedAt());
        res.setFinishedAt(session.getFinishedAt());
        res.setProfile(parseProfile(session.getProfileJson()));
        return res;
    }

    private JSONObject parseProfile(String profileJson) {
        if (isBlank(profileJson)) {
            return null;
        }
        try {
            return JSON.parseObject(profileJson);
        } catch (Exception e) {
            return null;
        }
    }

    private String buildVoicePrompt(JSONObject profile, List<InterviewQuestionBankEntity> questions) {
        StringBuilder builder = new StringBuilder();
        builder.append("你是一名模拟面试官，正在进行一场实时语音模拟面试。\n");
        builder.append("请基于候选人的面试画像提问，不要一次性列出所有问题。\n");
        builder.append("面试规则：\n");
        builder.append("1. 一次只问一个问题，语气自然，像真人面试官。\n");
        builder.append("2. 先让候选人做简短自我介绍，再围绕项目、技术栈、个人职责逐步追问。\n");
        builder.append("3. 如果候选人回答太短，要继续追问细节；如果回答不清楚，要换一种问法确认。\n");
        builder.append("4. 优先从下方已审核面试题中选择与当前对话最相关的问题，并根据候选人的回答自然追问。\n");
        builder.append("5. 不要直接透露完整评分标准，也不要替候选人回答。\n\n");
        builder.append("候选人画像：\n");
        appendProfileLine(builder, "目标方向", profile.getString("targetPosition"));
        appendProfileLine(builder, "经验阶段", profile.getString("level"));
        appendProfileLine(builder, "建议面试类型", profile.getString("suggestedInterviewType"));
        appendProfileArray(builder, "技能标签", profile.getJSONArray("skills"));
        appendProfileArray(builder, "推荐题目方向", profile.getJSONArray("questionTags"));
        appendProjects(builder, profile.getJSONArray("projects"));
        appendProfileArray(builder, "需要重点确认的风险点", profile.getJSONArray("riskPoints"));
        appendQuestionBank(builder, questions);
        builder.append("\n现在请用中文开场，邀请候选人进行 1 分钟自我介绍。");
        return builder.toString();
    }

    private String buildQuestionRetrievalQuery(JSONObject profile) {
        StringBuilder query = new StringBuilder();
        appendQueryValue(query, profile.getString("targetPosition"));
        appendQueryValue(query, profile.getString("level"));
        appendQueryArray(query, profile.getJSONArray("skills"));
        appendQueryArray(query, profile.getJSONArray("questionTags"));
        JSONArray projects = profile.getJSONArray("projects");
        if (projects != null) {
            for (int i = 0; i < projects.size(); i++) {
                JSONObject project = projects.getJSONObject(i);
                if (project == null) continue;
                appendQueryValue(query, project.getString("name"));
                appendQueryArray(query, project.getJSONArray("techStack"));
                appendQueryArray(query, project.getJSONArray("interviewFocus"));
            }
        }
        return limitText(query.toString().trim(), 3000);
    }

    private void appendQueryValue(StringBuilder query, String value) {
        if (!isBlank(value)) query.append(value.trim()).append('\n');
    }

    private void appendQueryArray(StringBuilder query, JSONArray values) {
        if (values == null) return;
        for (String value : values.toJavaList(String.class)) appendQueryValue(query, value);
    }

    private void appendQuestionBank(StringBuilder builder, List<InterviewQuestionBankEntity> questions) {
        if (questions == null || questions.isEmpty()) {
            builder.append("\n正式题库当前没有可用题目，请根据候选人画像完成本轮面试。\n");
            return;
        }
        builder.append("\n已审核面试题（仅供面试官使用，不得向候选人透露参考答案）：\n");
        for (int i = 0; i < questions.size(); i++) {
            InterviewQuestionBankEntity question = questions.get(i);
            builder.append(i + 1).append(". [")
                    .append(isBlank(question.getCategory()) ? "未分类" : question.getCategory())
                    .append("] ").append(question.getNormalizedQuestion()).append("\n");
            String answer = isBlank(question.getShortAnswer()) ? question.getStandardAnswer() : question.getShortAnswer();
            if (!isBlank(answer)) {
                builder.append("   回答要点：").append(limitText(answer.trim(), 600)).append("\n");
            }
            JSONArray followUps = parseJsonArray(question.getFollowUpQuestions());
            if (followUps != null && !followUps.isEmpty()) {
                List<String> values = followUps.toJavaList(String.class);
                builder.append("   可选追问：")
                        .append(String.join("；", values.subList(0, Math.min(values.size(), 3))))
                        .append("\n");
            }
        }
        builder.append("使用要求：结合自我介绍和前序回答按需选题；一次只问一道；不要机械地按顺序全部提问；已经问过的题不要重复。\n");
    }

    private JSONArray parseJsonArray(String value) {
        try {
            return isBlank(value) ? new JSONArray() : JSON.parseArray(value);
        } catch (Exception ignored) {
            return new JSONArray();
        }
    }

    private void appendProfileLine(StringBuilder builder, String label, String value) {
        if (!isBlank(value)) {
            builder.append("- ").append(label).append("：").append(value).append("\n");
        }
    }

    private void appendProfileArray(StringBuilder builder, String label, JSONArray values) {
        if (values != null && !values.isEmpty()) {
            builder.append("- ").append(label).append("：").append(String.join("、", values.toJavaList(String.class))).append("\n");
        }
    }

    private void appendProjects(StringBuilder builder, JSONArray projects) {
        if (projects == null || projects.isEmpty()) {
            return;
        }
        builder.append("- 项目经历：\n");
        for (int i = 0; i < projects.size(); i++) {
            JSONObject project = projects.getJSONObject(i);
            if (project == null) {
                continue;
            }
            builder.append("  ").append(i + 1).append(". ");
            builder.append(project.getString("name") == null ? "未命名项目" : project.getString("name"));
            if (!isBlank(project.getString("role"))) {
                builder.append("，职责：").append(project.getString("role"));
            }
            JSONArray focus = project.getJSONArray("interviewFocus");
            if (focus != null && !focus.isEmpty()) {
                builder.append("，追问重点：").append(String.join("、", focus.toJavaList(String.class)));
            }
            builder.append("\n");
        }
    }

    private EduStudentEntity getStudent(Long studentId) {
        if (studentId == null) {
            throw BusinessException.UserNotExist.newInstance("学生不能为空");
        }
        EduStudentEntity student = studentMapper.selectById(studentId);
        if (student == null) {
            throw BusinessException.UserNotExist.newInstance("学生不存在");
        }
        return student;
    }

    private String limitText(String value, int limit) {
        if (value == null || value.length() <= limit) {
            return value;
        }
        return value.substring(0, limit);
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && keyword != null && text.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
