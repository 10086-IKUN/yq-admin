package cn.yanque.models.interview.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.file.service.FileService;
import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.interview.client.DoubaoAsrClient;
import cn.yanque.models.interview.client.InterviewPythonClient;
import cn.yanque.models.interview.mapper.InterviewReviewRecordMapper;
import cn.yanque.models.interview.pojo.InterviewReviewDtos;
import cn.yanque.models.interview.pojo.InterviewReviewRecordEntity;
import cn.yanque.models.interview.service.InterviewReviewService;
import cn.yanque.models.interview.service.InterviewQuestionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InterviewReviewServiceImpl implements InterviewReviewService {
    private static final String TRANSCRIBING = "TRANSCRIBING";
    private static final String ANALYZING = "ANALYZING";

    @Autowired
    private InterviewReviewRecordMapper mapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private DoubaoAsrClient asrClient;
    @Autowired
    private InterviewPythonClient pythonClient;
    @Autowired
    private InterviewQuestionService questionService;
    @Autowired
    private EduStudentMapper studentMapper;

    @Override
    public PageResult<InterviewReviewDtos.Item> page(InterviewReviewDtos.PageReq req) {
        normalizePage(req);
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<InterviewReviewRecordEntity> entities = mapper.selectPage(req);
        PageInfo<InterviewReviewRecordEntity> info = new PageInfo<>(entities);
        return new PageResult<>(info.getTotal(), req.getPageNum(), req.getPageSize(), entities.stream().map(this::toItem).toList());
    }

    @Override
    public InterviewReviewDtos.Item create(Long operatorId, InterviewReviewDtos.CreateReq req, boolean autoTranscribe) {
        if (req.getStudentId() == null) throw new BusinessException(400, "学生不能为空");
        validateAudioKey(req.getAudioObjectKey());
        InterviewReviewRecordEntity entity = new InterviewReviewRecordEntity();
        BeanUtils.copyProperties(req, entity);
        if (entity.getResumeText() == null || entity.getResumeText().isBlank()) {
            EduStudentEntity student = studentMapper.selectById(req.getStudentId());
            if (student != null && "SUCCESS".equals(student.getResumeParseStatus())) {
                entity.setResumeText(student.getResumeText());
            }
        }
        entity.setStatus("PENDING");
        entity.setCreatedBy(operatorId);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(entity.getCreatedAt());
        mapper.insert(entity);
        if (autoTranscribe) {
            try {
                submit(entity);
            } catch (Exception ex) {
                mapper.markFailed(entity.getId(), safeMessage(ex), new Date());
            }
        }
        return toItem(get(entity.getId()));
    }

    @Override
    public InterviewReviewDtos.Item detail(Long id) {
        return toItem(get(id));
    }

    @Override
    public void retry(Long id, Long studentId) {
        InterviewReviewRecordEntity record = get(id);
        verifyOwner(record, studentId);
        if (TRANSCRIBING.equals(record.getStatus()) || ANALYZING.equals(record.getStatus())) {
            throw new BusinessException(400, "该面试正在处理中");
        }
        if (record.getTranscriptDialogueJson() != null && !record.getTranscriptDialogueJson().isBlank()) {
            analyze(record, record.getTranscriptDialogueJson());
        } else {
            submit(record);
        }
    }

    @Override
    public void pollTranscribing() {
        for (InterviewReviewRecordEntity record : mapper.selectTranscribing(asrClient.batchSize())) {
            try {
                DoubaoAsrClient.QueryResult result = asrClient.query(record.getTranscriptTaskId());
                if (result.status() == DoubaoAsrClient.Status.PROCESSING) continue;
                if (result.status() == DoubaoAsrClient.Status.FAILED) {
                    mapper.markFailed(record.getId(), friendlyError(result.errorMessage()), new Date());
                    continue;
                }
                mapper.markAnalyzing(record.getId(), result.dialogueJson(), new Date());
                analyze(get(record.getId()), result.dialogueJson());
            } catch (Exception ex) {
                mapper.markFailed(record.getId(), safeMessage(ex), new Date());
            }
        }
    }

    private void submit(InterviewReviewRecordEntity record) {
        String audioUrl = fileService.preview(record.getAudioObjectKey());
        String taskId = asrClient.submit(record.getStudentId(), audioUrl, record.getAudioFileName());
        mapper.markTranscribing(record.getId(), taskId, new Date());
    }

    private void analyze(InterviewReviewRecordEntity record, String rawDialogue) {
        try {
            String dialogue = pythonClient.polish(rawDialogue);
            String report = pythonClient.generateReport(record, dialogue);
            mapper.markDone(record.getId(), dialogue, report, new Date());
            questionService.processReview(record.getId());
        } catch (Exception ex) {
            mapper.markFailed(record.getId(), safeMessage(ex), new Date());
        }
    }

    private InterviewReviewRecordEntity get(Long id) {
        InterviewReviewRecordEntity entity = id == null ? null : mapper.selectById(id);
        if (entity == null) throw new BusinessException(404, "面试复盘记录不存在");
        return entity;
    }

    private void verifyOwner(InterviewReviewRecordEntity entity, Long studentId) {
        if (studentId != null && !studentId.equals(entity.getStudentId())) {
            throw new BusinessException(403, "只能操作自己的面试复盘");
        }
    }

    private void validateAudioKey(String key) {
        if (key == null || key.isBlank() || key.contains("..") || !key.replace('\\', '/').contains("/interview-review/")) {
            throw new BusinessException(400, "面试录音路径不合法");
        }
    }

    private void normalizePage(InterviewReviewDtos.PageReq req) {
        if (req.getPageNum() == null || req.getPageNum() < 1) req.setPageNum(1);
        if (req.getPageSize() == null || req.getPageSize() < 1) req.setPageSize(20);
        if (req.getPageSize() > 100) req.setPageSize(100);
    }

    private InterviewReviewDtos.Item toItem(InterviewReviewRecordEntity entity) {
        InterviewReviewDtos.Item item = new InterviewReviewDtos.Item();
        BeanUtils.copyProperties(entity, item);
        return item;
    }

    private String safeMessage(Exception ex) {
        String value = ex.getMessage() == null || ex.getMessage().isBlank() ? "处理失败" : ex.getMessage();
        return friendlyError(value);
    }

    /** 将第三方服务的技术错误转换成学生和老师都能理解的失败原因。 */
    private String friendlyError(String rawMessage) {
        String raw = rawMessage == null ? "" : rawMessage.trim();
        String lower = raw.toLowerCase();
        String friendly;
        if (lower.contains("normal silence audio") || lower.contains("no valid speech")
                || lower.contains("no speech") || lower.contains("silence")) {
            friendly = "未识别到有效人声。请确认录音已开启麦克风或系统声音，且人声清晰、音量正常；如果上传的是屏幕录像，请确认录像中确实包含可听见的人声。";
        } else if (lower.contains("format") || lower.contains("codec") || lower.contains("decode")
                || lower.contains("invalid audio")) {
            friendly = "录音格式或编码不受支持。请转换为 MP3、WAV、M4A、AAC 或 OGG 后重新上传。";
        } else if (lower.contains("download") || lower.contains("url") || lower.contains("403")
                || lower.contains("access denied")) {
            friendly = "语音服务无法读取录音文件。录音访问地址可能已过期或没有访问权限，请重新上传后再试。";
        } else if (lower.contains("timeout") || lower.contains("timed out")) {
            friendly = "语音转写处理超时。录音可能较大或服务繁忙，请稍后点击重试。";
        } else if (lower.contains("quota") || lower.contains("balance") || lower.contains("limit exceeded")) {
            friendly = "豆包语音识别额度不足或已达到调用上限，请联系管理员检查服务额度。";
        } else if (lower.contains("unauthorized") || lower.contains("forbidden")
                || lower.contains("api key") || lower.contains("access key")) {
            friendly = "豆包语音识别鉴权失败，请联系管理员检查 API Key 和服务授权。";
        } else if (lower.contains("python") || lower.contains("ai 对话") || lower.contains("ai 面试")) {
            friendly = "录音已完成转写，但 AI 对话整理或复盘报告生成失败，请稍后重试。";
        } else if (raw.isBlank()) {
            friendly = "处理失败，但第三方服务没有返回具体原因，请稍后重试或联系管理员查看日志。";
        } else {
            friendly = "处理失败：" + raw;
        }
        return friendly.substring(0, Math.min(friendly.length(), 1000));
    }
}
