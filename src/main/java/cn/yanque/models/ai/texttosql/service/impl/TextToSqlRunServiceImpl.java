package cn.yanque.models.ai.texttosql.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.texttosql.mapper.TextToSqlRunMapper;
import cn.yanque.models.ai.texttosql.mapper.TextToSqlRunStepMapper;
import cn.yanque.models.ai.texttosql.pojo.bo.TextToSqlRunQueryBo;
import cn.yanque.models.ai.texttosql.pojo.dto.TextToSqlRouteDto;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlRunEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlRunStepEntity;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlRunPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlRunRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlRunStepRes;
import cn.yanque.models.ai.texttosql.service.TextToSqlRunService;
import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class TextToSqlRunServiceImpl implements TextToSqlRunService {

    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_INTERRUPTED = "INTERRUPTED";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";
    private static final String SOURCE_USER = "USER";
    private static final int QUERY_ROW_SAMPLE_SIZE = 20;

    @Autowired
    private TextToSqlRunMapper textToSqlRunMapper;

    @Autowired
    private TextToSqlRunStepMapper textToSqlRunStepMapper;

    @Override
    public void createRunning(String conversationId, String originalQuestion, Long createdBy) {
        createRunning(conversationId, originalQuestion, createdBy, SOURCE_USER);
    }

    @Override
    public void createRunning(String conversationId, String originalQuestion, Long createdBy, String sourceType) {
        // 同一个 conversationId 可能经历多轮澄清/继续执行；已有记录时只恢复 RUNNING，不重复插入主记录。
        TextToSqlRunEntity existed = textToSqlRunMapper.selectByConversationId(conversationId);
        if (existed != null) {
            textToSqlRunMapper.updateStatusByConversationId(conversationId, STATUS_RUNNING);
            return;
        }

        // 首轮提问时先落一条 RUNNING 记录，后续 Python graph 返回结果后再补全 state、SQL、回答等字段。
        Date now = new Date();
        TextToSqlRunEntity entity = new TextToSqlRunEntity();
        entity.setConversationId(conversationId);
        entity.setSourceType(normalizeSourceType(sourceType));
        entity.setOriginalQuestion(originalQuestion);
        entity.setFinalQuestion(originalQuestion);
        entity.setStatus(STATUS_RUNNING);
        entity.setInterrupted(false);
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        textToSqlRunMapper.insert(entity);
    }

    @Override
    public void markRunning(String conversationId) {
        // 用户补充澄清后继续执行 graph，先把历史记录重新标记为 RUNNING。
        textToSqlRunMapper.updateStatusByConversationId(conversationId, STATUS_RUNNING);
    }

    @Override
    public void saveResultAsync(String conversationId, TextToSqlRouteDto.RouteRes res) {
        // 保存运行记录不阻塞接口响应；即使保存失败，也不影响本次 Text-to-SQL 结果返回给前端。
        CompletableFuture.runAsync(() -> {
            try {
                saveResult(conversationId, res);
            } catch (Exception ex) {
                log.warn("保存 Text-to-SQL 运行记录失败，conversationId={}", conversationId, ex);
            }
        });
    }

    @Override
    public void saveFailure(String conversationId, String errorMessage) {
        // 远程 Text-to-SQL 服务异常时仍要落库，方便运行记录页面排查失败会话。
        Date now = new Date();
        TextToSqlRunEntity entity = new TextToSqlRunEntity();
        entity.setConversationId(conversationId);
        entity.setSourceType(SOURCE_USER);
        entity.setStatus(STATUS_FAILED);
        entity.setInterrupted(false);
        entity.setExecutionError(errorMessage);
        entity.setFinalAnswer(errorMessage);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        // 正常情况下 createRunning 已经先插入记录；如果异常发生得更早，这里兜底插入一条失败记录。
        if (textToSqlRunMapper.updateByConversationId(entity) == 0) {
            entity.setOriginalQuestion("");
            textToSqlRunMapper.insert(entity);
        }
    }

    @Override
    public PageResult<TextToSqlRunRes> page(TextToSqlRunPageReq req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        TextToSqlRunQueryBo query = new TextToSqlRunQueryBo();
        BeanUtils.copyProperties(req, query);
        query.setKeyword(normalizeBlank(query.getKeyword()));
        query.setConversationId(normalizeBlank(query.getConversationId()));
        query.setSourceType(normalizeSourceType(query.getSourceType()));
        query.setQuestionType(normalizeBlank(query.getQuestionType()));
        query.setBusinessId(normalizeBlank(query.getBusinessId()));
        query.setStatus(normalizeBlank(query.getStatus()));
        query.setSqlAction(normalizeBlank(query.getSqlAction()));
        query.setFeedbackResult(normalizeBlank(query.getFeedbackResult()));
        // PageHelper 必须紧贴主查询调用，避免后续辅助查询被错误分页。
        PageHelper.startPage(pageNum, pageSize);
        List<TextToSqlRunEntity> list = textToSqlRunMapper.selectPage(query);
        PageInfo<TextToSqlRunEntity> pageInfo = new PageInfo<>(list);
        List<TextToSqlRunRes> records = list.stream().map(this::buildRunRes).toList();
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, records);
    }

    @Override
    public TextToSqlRunRes detail(Long id) {
        TextToSqlRunEntity entity = textToSqlRunMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.DateError.newInstance("运行记录不存在");
        }
        TextToSqlRunRes res = buildRunRes(entity);
        res.setSteps(textToSqlRunStepMapper.selectByRunId(id).stream().map(this::buildStepRes).toList());
        return res;
    }

    private void saveResult(String conversationId, TextToSqlRouteDto.RouteRes res) {
        Date now = new Date();
        // stateSnapshot 是 graph 的最终状态，字段比 RouteRes 更完整；保存运行记录时优先从这里还原最终问题。
        Map<String, Object> snapshot = res.getStateSnapshot() == null ? Collections.emptyMap() : res.getStateSnapshot();
        TextToSqlRunEntity entity = new TextToSqlRunEntity();
        entity.setConversationId(conversationId);
        entity.setFinalQuestion(toStringValue(snapshot.get("user_question")));
        entity.setOriginalQuestion(toStringValue(snapshot.get("original_question")));
        entity.setQuestionType(res.getQuestionType());
        entity.setBusinessId(res.getBusinessId());
        entity.setBusinessName(res.getBusinessName());
        entity.setRouteReason(res.getRouteReason());
        entity.setStatus(Boolean.TRUE.equals(res.getInterrupted()) ? STATUS_INTERRUPTED : STATUS_COMPLETED);
        entity.setInterrupted(Boolean.TRUE.equals(res.getInterrupted()) || hasClarification(snapshot));
        entity.setSqlAction(res.getAction());
        entity.setGeneratedSql(res.getSql());
        entity.setExecutedSql(res.getExecutedSql());
        entity.setSqlGenerationReason(res.getReason());
        entity.setExecutionError(res.getExecutionError());
        entity.setUsedTables(toJson(res.getUsedTables()));
        entity.setUsedFields(toJson(res.getUsedFields()));
        entity.setMissingInfo(toJson(res.getMissingInfo()));
        entity.setQueryColumns(toJson(res.getColumns()));
        // 查询结果可能很多，运行记录只保存前 N 行样例，避免大结果集撑爆运行记录表。
        entity.setQueryRowsSample(toJson(sampleRows(res.getRows())));
        entity.setQueryRowCount(res.getRowCount());
        entity.setAnalysisSummary(res.getAnalysis() == null ? null : res.getAnalysis().getSummary());
        entity.setAnalysisJson(toJson(res.getAnalysis()));
        entity.setFinalAnswer(res.getFinalAnswer());
        String snapshotJson = toJson(snapshot);
        entity.setStateSnapshotJson(snapshotJson);
        entity.setStateSnapshotText(snapshotJson);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        // createRunning 已经插入主记录时走 update；如果缺少预创建记录则兜底 insert。
        if (textToSqlRunMapper.updateByConversationId(entity) == 0) {
            if (entity.getOriginalQuestion() == null || entity.getOriginalQuestion().isBlank()) {
                entity.setOriginalQuestion(entity.getFinalQuestion() == null ? "" : entity.getFinalQuestion());
            }
            textToSqlRunMapper.insert(entity);
        }
        TextToSqlRunEntity savedRun = textToSqlRunMapper.selectByConversationId(conversationId);
        // stateHistory 保存每个 graph 节点后的快照，是排查路由、选表、澄清判断错误的核心证据。
        saveStateHistory(savedRun, res.getStateHistory());
    }

    private void saveStateHistory(TextToSqlRunEntity run, List<Map<String, Object>> stateHistory) {
        if (run == null || run.getId() == null) {
            return;
        }
        // 同一个 conversationId 多轮执行会产生新的 history；先删旧步骤，再保存最新完整链路。
        textToSqlRunStepMapper.deleteByRunId(run.getId());
        if (stateHistory == null || stateHistory.isEmpty()) {
            return;
        }

        Date now = new Date();
        List<TextToSqlRunStepEntity> steps = new ArrayList<>();
        for (Map<String, Object> item : stateHistory) {
            TextToSqlRunStepEntity step = new TextToSqlRunStepEntity();
            step.setRunId(run.getId());
            step.setConversationId(run.getConversationId());
            step.setStepIndex(toInteger(item.get("stepIndex")));
            step.setNodeName(toStringValue(item.get("nodeName")));
            step.setSource(toStringValue(item.get("source")));
            step.setLangGraphStep(toInteger(item.get("langGraphStep")));
            step.setNextNodes(toJson(item.get("nextNodes")));
            step.setStateSnapshotJson(toJson(item.get("stateSnapshot")));
            step.setCheckpointCreatedAt(toStringValue(item.get("createdAt")));
            step.setSavedAt(now);
            steps.add(step);
        }
        if (!steps.isEmpty()) {
            textToSqlRunStepMapper.insertBatch(steps);
        }
    }

    private List<Map<String, Object>> sampleRows(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }
        if (rows.size() <= QUERY_ROW_SAMPLE_SIZE) {
            return rows;
        }
        return rows.subList(0, QUERY_ROW_SAMPLE_SIZE);
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        return JSON.toJSONString(value);
    }

    private String toStringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private boolean hasClarification(Map<String, Object> snapshot) {
        // 有些流程最终 interrupted=false，但历史中发生过澄清；运行记录用 clarification_history 保留“曾澄清”事实。
        Object history = snapshot.get("clarification_history");
        return history instanceof List<?> list && !list.isEmpty();
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private TextToSqlRunRes buildRunRes(TextToSqlRunEntity entity) {
        TextToSqlRunRes res = new TextToSqlRunRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private TextToSqlRunStepRes buildStepRes(TextToSqlRunStepEntity entity) {
        TextToSqlRunStepRes res = new TextToSqlRunStepRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private String normalizeBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeSourceType(String value) {
        String normalized = normalizeBlank(value);
        // 运行记录页面默认看真实用户记录；评测任务会显式传 EVAL。
        return normalized == null ? SOURCE_USER : normalized;
    }
}

