package cn.yanque.models.interview.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.interview.client.InterviewPythonClient;
import cn.yanque.models.interview.mapper.InterviewQuestionMapper;
import cn.yanque.models.interview.mapper.InterviewReviewRecordMapper;
import cn.yanque.models.interview.pojo.InterviewQuestionBankEntity;
import cn.yanque.models.interview.pojo.InterviewQuestionDtos;
import cn.yanque.models.interview.pojo.InterviewQuestionSourceEntity;
import cn.yanque.models.interview.pojo.InterviewReviewRecordEntity;
import cn.yanque.models.interview.service.InterviewQuestionService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

@Service
public class InterviewQuestionServiceImpl implements InterviewQuestionService {
    private static final Logger log = LoggerFactory.getLogger(InterviewQuestionServiceImpl.class);
    private static final double AUTO_MERGE_SCORE = 0.90D;
    private static final Set<String> AUDIT_STATUSES = Set.of("PENDING", "PUBLISHED", "REJECTED");

    @Autowired private InterviewQuestionMapper questionMapper;
    @Autowired private InterviewReviewRecordMapper reviewMapper;
    @Autowired private InterviewPythonClient pythonClient;

    @Override
    public void processReview(Long reviewId) {
        InterviewReviewRecordEntity review = reviewMapper.selectById(reviewId);
        if (review == null || !"DONE".equals(review.getStatus())) {
            throw new BusinessException(400, "只有复盘完成后才能生成面试题库");
        }
        Date now = new Date();
        reviewMapper.markQuestionProcessing(reviewId, now);
        int created = 0;
        int merged = 0;
        try {
            JSONArray candidates = pythonClient.extractQuestions(review);
            String vectorFailure = null;
            for (Object value : candidates) {
                JSONObject candidate = (JSONObject) value;
                String normalized = text(candidate, "normalizedQuestion", 500);
                String original = text(candidate, "originalQuestion", 1000);
                if (normalized.isBlank() || original.isBlank()) continue;

                InterviewQuestionBankEntity question = questionMapper.selectByNormalizedQuestion(normalized);
                if (question == null) {
                    JSONObject hit = pythonClient.findSimilarQuestion(normalized);
                    if (hit != null && hit.getDoubleValue("score") >= AUTO_MERGE_SCORE) {
                        InterviewQuestionBankEntity matched = questionMapper.selectById(hit.getLong("questionId"));
                        if (matched != null && categoryCompatible(matched.getCategory(), candidate.getString("category"))) {
                            question = matched;
                        }
                    }
                }

                boolean isNew = question == null;
                if (isNew) {
                    question = buildQuestion(candidate, reviewId, now);
                    questionMapper.insertQuestion(question);
                    created++;
                }

                InterviewQuestionSourceEntity source = buildSource(candidate, question.getId(), reviewId, now);
                if (questionMapper.insertSource(source) > 0) {
                    questionMapper.incrementSource(question.getId(), reviewId, now);
                    if (!isNew) merged++;
                }

                try {
                    pythonClient.upsertQuestionVector(question.getId(), question.getNormalizedQuestion(),
                            question.getCategory(), parseArray(question.getTags()));
                    questionMapper.markVector(question.getId(), "INDEXED", String.valueOf(question.getId()));
                } catch (Exception vectorEx) {
                    questionMapper.markVector(question.getId(), "FAILED", null);
                    vectorFailure = concise(vectorEx);
                }
            }
            reviewMapper.markQuestionDone(reviewId, candidates.size(), created, merged, new Date());
            if (vectorFailure != null) {
                reviewMapper.markQuestionFailed(reviewId, "题目已保存，但写入向量数据库失败：" + vectorFailure, new Date());
            }
        } catch (Exception ex) {
            reviewMapper.markQuestionFailed(reviewId, friendlyReason(ex), new Date());
        }
    }

    @Override
    public PageResult<InterviewQuestionDtos.Item> page(InterviewQuestionDtos.PageReq req) {
        if (req.getPageNum() == null || req.getPageNum() < 1) req.setPageNum(1);
        if (req.getPageSize() == null || req.getPageSize() < 1) req.setPageSize(20);
        req.setPageSize(Math.min(req.getPageSize(), 100));
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<InterviewQuestionBankEntity> rows = questionMapper.selectPage(req);
        PageInfo<InterviewQuestionBankEntity> info = new PageInfo<>(rows);
        return new PageResult<>(info.getTotal(), req.getPageNum(), req.getPageSize(), rows.stream().map(this::toItem).toList());
    }

    @Override
    public InterviewQuestionDtos.Detail detail(Long id) {
        InterviewQuestionBankEntity entity = requireQuestion(id);
        InterviewQuestionDtos.Detail detail = new InterviewQuestionDtos.Detail();
        detail.setQuestion(toItem(entity));
        detail.setSources(questionMapper.selectSources(id).stream().map(this::toSourceItem).toList());
        return detail;
    }

    @Override
    public void audit(Long id, String status) {
        requireQuestion(id);
        String normalized = status == null ? "" : status.trim().toUpperCase();
        if (!AUDIT_STATUSES.contains(normalized)) throw new BusinessException(400, "审核状态不合法");
        questionMapper.updateAuditStatus(id, normalized);
    }

    @Override
    public List<InterviewQuestionBankEntity> recommendPublishedForMockInterview(String profileQuery, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 12));
        List<InterviewQuestionBankEntity> candidates = questionMapper.selectPublishedCandidates(200);
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        Map<Long, InterviewQuestionBankEntity> byId = new LinkedHashMap<>();
        for (InterviewQuestionBankEntity candidate : candidates) {
            byId.put(candidate.getId(), candidate);
        }

        LinkedHashMap<Long, InterviewQuestionBankEntity> selected = new LinkedHashMap<>();
        if (profileQuery != null && !profileQuery.isBlank()) {
            try {
                JSONArray hits = pythonClient.searchSimilarQuestions(profileQuery, Math.max(10, safeLimit * 3));
                for (Object value : hits) {
                    JSONObject hit = (JSONObject) value;
                    InterviewQuestionBankEntity candidate = byId.get(hit.getLong("questionId"));
                    if (candidate != null) {
                        selected.putIfAbsent(candidate.getId(), candidate);
                        if (selected.size() >= safeLimit) {
                            return new ArrayList<>(selected.values());
                        }
                    }
                }
            } catch (Exception ex) {
                log.warn("Mock interview vector retrieval failed; using MySQL fallback: {}", concise(ex));
            }
        }

        candidates.stream()
                .sorted(Comparator
                        .comparingInt((InterviewQuestionBankEntity item) -> lexicalScore(item, profileQuery)).reversed()
                        .thenComparing(item -> item.getSourceCount() == null ? 0 : item.getSourceCount(), Comparator.reverseOrder())
                        .thenComparing(InterviewQuestionBankEntity::getId, Comparator.reverseOrder()))
                .forEach(item -> {
                    if (selected.size() < safeLimit) {
                        selected.putIfAbsent(item.getId(), item);
                    }
                });
        return new ArrayList<>(selected.values());
    }

    private int lexicalScore(InterviewQuestionBankEntity item, String profileQuery) {
        if (profileQuery == null || profileQuery.isBlank()) {
            return 0;
        }
        String haystack = String.join(" ",
                item.getNormalizedQuestion() == null ? "" : item.getNormalizedQuestion(),
                item.getCategory() == null ? "" : item.getCategory(),
                item.getTags() == null ? "" : item.getTags()).toLowerCase(Locale.ROOT);
        return Arrays.stream(profileQuery.toLowerCase(Locale.ROOT).split("[\\s,，、;；/|]+"))
                .map(String::trim)
                .filter(term -> term.length() >= 2)
                .mapToInt(term -> haystack.contains(term) ? 1 : 0)
                .sum();
    }

    private InterviewQuestionBankEntity buildQuestion(JSONObject value, Long reviewId, Date now) {
        InterviewQuestionBankEntity entity = new InterviewQuestionBankEntity();
        entity.setNormalizedQuestion(text(value, "normalizedQuestion", 500));
        entity.setCategory(text(value, "category", 100));
        entity.setTags(json(value.getJSONArray("tags")));
        entity.setStandardAnswer(text(value, "standardAnswer", 10000));
        entity.setShortAnswer(text(value, "shortAnswer", 10000));
        entity.setFollowUpQuestions(json(value.getJSONArray("followUpQuestions")));
        entity.setPitfalls(json(value.getJSONArray("pitfalls")));
        entity.setAuditStatus("PENDING");
        entity.setVectorStatus("PENDING");
        entity.setConfidence(BigDecimal.valueOf(value.getDoubleValue("confidence")));
        entity.setFirstSeenAt(now); entity.setLastSeenAt(now); entity.setLastSourceReviewId(reviewId);
        entity.setCreatedAt(now); entity.setUpdatedAt(now);
        return entity;
    }

    private InterviewQuestionSourceEntity buildSource(JSONObject value, Long questionId, Long reviewId, Date now) {
        InterviewQuestionSourceEntity entity = new InterviewQuestionSourceEntity();
        entity.setQuestionId(questionId); entity.setReviewRecordId(reviewId);
        entity.setOriginalQuestion(text(value, "originalQuestion", 1000));
        entity.setAnswerContext(text(value, "answerContext", 10000));
        entity.setStudentAnswerQuality(text(value, "studentAnswerQuality", 32));
        Integer score = value.getInteger("studentAnswerScore");
        entity.setStudentAnswerScore(score == null ? null : Math.max(0, Math.min(score, 100)));
        entity.setStudentAnswerAnalysis(text(value, "studentAnswerAnalysis", 10000));
        entity.setImprovementSuggestion(text(value, "improvementSuggestion", 10000));
        entity.setConfidence(BigDecimal.valueOf(value.getDoubleValue("confidence")));
        entity.setSourceHash(sha256(entity.getOriginalQuestion())); entity.setCreatedAt(now);
        return entity;
    }

    private InterviewQuestionBankEntity requireQuestion(Long id) {
        InterviewQuestionBankEntity entity = id == null ? null : questionMapper.selectById(id);
        if (entity == null) throw new BusinessException(404, "面试题目不存在");
        return entity;
    }

    private InterviewQuestionDtos.Item toItem(InterviewQuestionBankEntity entity) {
        InterviewQuestionDtos.Item item = new InterviewQuestionDtos.Item();
        BeanUtils.copyProperties(entity, item); return item;
    }

    private InterviewQuestionDtos.SourceItem toSourceItem(InterviewQuestionSourceEntity entity) {
        InterviewQuestionDtos.SourceItem item = new InterviewQuestionDtos.SourceItem();
        BeanUtils.copyProperties(entity, item); return item;
    }

    private boolean categoryCompatible(String left, String right) {
        return left == null || right == null || left.isBlank() || right.isBlank() || "其他".equals(left) || "其他".equals(right) || left.equalsIgnoreCase(right);
    }

    private String friendlyReason(Exception ex) {
        String raw = concise(ex);
        String lower = raw.toLowerCase();
        if (lower.contains("milvus") || lower.contains("19530") || lower.contains("vector")) return "向量数据库连接或检索失败：" + raw;
        if (lower.contains("timeout") || lower.contains("timed out")) return "题目生成请求超时，请稍后重试";
        if (lower.contains("json")) return "AI 返回的题目结构无法解析，请重试：" + raw;
        return "面试题目生成失败：" + raw;
    }

    private String concise(Exception ex) {
        String value = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        return value.substring(0, Math.min(value.length(), 700));
    }

    private String text(JSONObject value, String key, int max) {
        String text = value.getString(key);
        text = text == null ? "" : text.trim(); return text.substring(0, Math.min(text.length(), max));
    }

    private String json(JSONArray value) { return value == null ? "[]" : value.toJSONString(); }
    private JSONArray parseArray(String value) { try { return value == null ? new JSONArray() : com.alibaba.fastjson2.JSON.parseArray(value); } catch (Exception ignored) { return new JSONArray(); } }

    private String sha256(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) result.append(String.format("%02x", b));
            return result.toString();
        } catch (Exception ex) { throw new IllegalStateException(ex); }
    }
}
