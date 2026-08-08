package cn.yanque.models.ai.texttosql.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.system.config.service.SysConfig;
import cn.yanque.models.system.config.service.SysConfigService;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.texttosql.mapper.TextToSqlEvalAssertionMapper;
import cn.yanque.models.ai.texttosql.mapper.TextToSqlEvalQuestionMapper;
import cn.yanque.models.ai.texttosql.mapper.TextToSqlRunMapper;
import cn.yanque.models.ai.texttosql.pojo.bo.TextToSqlEvalQuestionQueryBo;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalAssertionEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalQuestionEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlRunEntity;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalAssertionReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalQuestionPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalQuestionSaveReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalQuestionUpdateReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalAssertionRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalMetadataRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalOptionRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalQuestionRes;
import cn.yanque.models.ai.texttosql.service.TextToSqlEvalQuestionService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TextToSqlEvalQuestionServiceImpl implements TextToSqlEvalQuestionService {

    private static final String SOURCE_MANUAL = "MANUAL";
    private static final String SOURCE_RUN_HISTORY = "RUN_HISTORY";
    private static final String SOURCE_FEEDBACK = "FEEDBACK";
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String TARGET_END_TO_END = "END_TO_END";
    private static final String CATEGORY_NORMAL = "NORMAL";
    private static final String OPERATOR_EQ = "EQ";
    private static final String OPERATOR_SEMANTIC = "SEMANTIC";
    private static final Set<String> SUPPORT_STATUS = Set.of(STATUS_DRAFT, "ACTIVE", "DISABLED");
    private static final Set<String> ENGINE_SUPPORT_OPERATOR = Set.of(
            OPERATOR_EQ, "CONTAINS", "NOT_CONTAINS", "EXISTS", "NOT_EMPTY", "REGEX", OPERATOR_SEMANTIC
    );

    @Autowired
    private TextToSqlEvalQuestionMapper textToSqlEvalQuestionMapper;

    @Autowired
    private TextToSqlEvalAssertionMapper textToSqlEvalAssertionMapper;

    @Autowired
    private TextToSqlRunMapper textToSqlRunMapper;

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public List<TextToSqlEvalQuestionEntity> selectActiveEntitiesByIds(List<Long> ids) {
        return textToSqlEvalQuestionMapper.selectActiveByIds(ids);
    }

    @Override
    public List<TextToSqlEvalAssertionEntity> selectAssertionEntitiesByQuestionId(Long evalQuestionId) {
        return textToSqlEvalAssertionMapper.selectByEvalQuestionId(evalQuestionId);
    }

    @Override
    public TextToSqlEvalMetadataRes metadata() {
        return loadMetadata();
    }

    @Override
    public PageResult<TextToSqlEvalQuestionRes> page(TextToSqlEvalQuestionPageReq req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        TextToSqlEvalQuestionQueryBo query = new TextToSqlEvalQuestionQueryBo();
        BeanUtils.copyProperties(req, query);
        // 前端 allowClear 会传空字符串；查询前统一归一化，避免 SQL 里出现无意义过滤条件。
        query.setKeyword(normalizeBlank(query.getKeyword()));
        query.setBusinessId(normalizeBlank(query.getBusinessId()));
        query.setEvalTarget(normalizeBlank(query.getEvalTarget()));
        query.setSampleCategory(normalizeBlank(query.getSampleCategory()));
        query.setSourceType(normalizeBlank(query.getSourceType()));
        query.setFeedbackResult(normalizeBlank(query.getFeedbackResult()));
        query.setStatus(normalizeBlank(query.getStatus()));

        PageHelper.startPage(pageNum, pageSize);
        List<TextToSqlEvalQuestionEntity> list = textToSqlEvalQuestionMapper.selectPage(query);
        PageInfo<TextToSqlEvalQuestionEntity> pageInfo = new PageInfo<>(list);
        Map<Long, List<TextToSqlEvalAssertionEntity>> assertionMap = selectAssertionMap(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize,
                list.stream().map(entity -> buildRes(entity, assertionMap.get(entity.getId()))).toList());
    }

    @Override
    public TextToSqlEvalQuestionRes detail(Long id) {
        TextToSqlEvalQuestionEntity entity = textToSqlEvalQuestionMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.DateError.newInstance("评测样本不存在");
        }
        return buildRes(entity, textToSqlEvalAssertionMapper.selectByEvalQuestionId(id));
    }

    @Override
    public IdRes create(TextToSqlEvalQuestionSaveReq req, Long createdBy) {
        Date now = new Date();
        TextToSqlEvalQuestionEntity entity = new TextToSqlEvalQuestionEntity();
        // 手动录入的样本没有来源运行记录，必须由整理人直接填写问题和判断标准。
        entity.setQuestion(normalizeRequired(req.getQuestion(), "问题不能为空"));
        entity.setBusinessId(normalizeBlank(req.getBusinessId()));
        entity.setBusinessName(normalizeBlank(req.getBusinessName()));
        entity.setEvalTarget(normalizeTarget(req.getEvalTarget()));
        entity.setSampleCategory(normalizeCategory(req.getSampleCategory()));
        entity.setSourceType(SOURCE_MANUAL);
        entity.setJudgeNote(normalizeBlank(req.getJudgeNote()));
        entity.setRemark(normalizeBlank(req.getRemark()));
        entity.setStatus(normalizeStatus(req.getStatus()));
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        textToSqlEvalQuestionMapper.insert(entity);
        replaceAssertions(entity.getId(), req.getAssertions());
        return new IdRes(entity.getId());
    }

    @Override
    public IdRes createFromRun(Long runId, Long createdBy) {
        // 同一条运行记录只允许沉淀一次样本，重复点击“加入样本”直接返回已有样本ID。
        TextToSqlEvalQuestionEntity existed = textToSqlEvalQuestionMapper.selectBySourceRunId(runId);
        if (existed != null) {
            return new IdRes(existed.getId());
        }

        TextToSqlRunEntity run = textToSqlRunMapper.selectById(runId);
        if (run == null) {
            throw BusinessException.DateError.newInstance("运行记录不存在");
        }
        String question = firstNotBlank(run.getFinalQuestion(), run.getOriginalQuestion());
        if (question == null) {
            throw BusinessException.DateError.newInstance("运行记录缺少问题，不能加入评测样本");
        }

        // 从运行记录生成的是候选样本：先带出原始结果和反馈信息，后续在样本页面人工整理为 ACTIVE。
        Date now = new Date();
        TextToSqlEvalQuestionEntity entity = new TextToSqlEvalQuestionEntity();
        entity.setQuestion(question);
        entity.setBusinessId(run.getBusinessId());
        entity.setBusinessName(run.getBusinessName());
        entity.setEvalTarget(inferEvalTarget(run));
        entity.setSampleCategory(run.getFeedbackResult() == null ? CATEGORY_NORMAL : "REGRESSION");
        entity.setSourceType(run.getFeedbackResult() == null ? SOURCE_RUN_HISTORY : SOURCE_FEEDBACK);
        entity.setSourceRunId(run.getId());
        entity.setSourceConversationId(run.getConversationId());
        entity.setGeneratedSql(run.getGeneratedSql());
        entity.setExecutedSql(run.getExecutedSql());
        entity.setFinalAnswer(run.getFinalAnswer());
        entity.setFeedbackResult(run.getFeedbackResult());
        entity.setFeedbackErrorType(run.getFeedbackErrorType());
        entity.setFeedbackComment(run.getFeedbackComment());
        entity.setJudgeNote(run.getFeedbackComment());
        entity.setStatus(STATUS_DRAFT);
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        textToSqlEvalQuestionMapper.insert(entity);
        replaceAssertions(entity.getId(), buildAssertionsFromRun(run));
        return new IdRes(entity.getId());
    }

    @Override
    public void update(Long id, TextToSqlEvalQuestionUpdateReq req) {
        TextToSqlEvalQuestionEntity existed = textToSqlEvalQuestionMapper.selectById(id);
        if (existed == null) {
            throw BusinessException.DateError.newInstance("评测样本不存在");
        }
        TextToSqlEvalQuestionEntity entity = new TextToSqlEvalQuestionEntity();
        entity.setId(id);
        entity.setQuestion(normalizeRequired(req.getQuestion(), "问题不能为空"));
        entity.setBusinessId(normalizeBlank(req.getBusinessId()));
        entity.setBusinessName(normalizeBlank(req.getBusinessName()));
        entity.setEvalTarget(normalizeTarget(req.getEvalTarget()));
        entity.setSampleCategory(normalizeCategory(req.getSampleCategory()));
        entity.setJudgeNote(normalizeBlank(req.getJudgeNote()));
        entity.setRemark(normalizeBlank(req.getRemark()));
        entity.setStatus(normalizeStatus(req.getStatus()));
        entity.setUpdatedAt(new Date());
        textToSqlEvalQuestionMapper.updateById(entity);
        replaceAssertions(id, req.getAssertions());
    }

    private TextToSqlEvalQuestionRes buildRes(TextToSqlEvalQuestionEntity entity, List<TextToSqlEvalAssertionEntity> assertions) {
        TextToSqlEvalQuestionRes res = new TextToSqlEvalQuestionRes();
        BeanUtils.copyProperties(entity, res);
        res.setAssertions(assertions == null ? List.of() : assertions.stream().map(this::buildAssertionRes).toList());
        return res;
    }

    private TextToSqlEvalAssertionRes buildAssertionRes(TextToSqlEvalAssertionEntity entity) {
        TextToSqlEvalAssertionRes res = new TextToSqlEvalAssertionRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private Map<Long, List<TextToSqlEvalAssertionEntity>> selectAssertionMap(List<TextToSqlEvalQuestionEntity> questions) {
        if (questions == null || questions.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = questions.stream().map(TextToSqlEvalQuestionEntity::getId).toList();
        return textToSqlEvalAssertionMapper.selectByEvalQuestionIds(ids)
                .stream()
                .collect(Collectors.groupingBy(TextToSqlEvalAssertionEntity::getEvalQuestionId, LinkedHashMap::new, Collectors.toList()));
    }

    private void replaceAssertions(Long evalQuestionId, List<TextToSqlEvalAssertionReq> reqList) {
        textToSqlEvalAssertionMapper.deleteByEvalQuestionId(evalQuestionId);
        List<TextToSqlEvalAssertionEntity> assertions = buildAssertionEntities(evalQuestionId, reqList);
        if (!assertions.isEmpty()) {
            textToSqlEvalAssertionMapper.insertBatch(assertions);
        }
    }

    private List<TextToSqlEvalAssertionEntity> buildAssertionEntities(Long evalQuestionId, List<TextToSqlEvalAssertionReq> reqList) {
        if (reqList == null || reqList.isEmpty()) {
            return List.of();
        }
        Date now = new Date();
        List<TextToSqlEvalAssertionEntity> result = new ArrayList<>();
        for (int i = 0; i < reqList.size(); i++) {
            TextToSqlEvalAssertionReq req = reqList.get(i);
            String operator = normalizeAssertionOperator(req.getOperator());
            String actualKey = normalizeAssertionKey(req.getActualKey(), operator);
            String expectedValue = normalizeBlank(req.getExpectedValue());
            if (requiresExpectedValue(operator) && expectedValue == null) {
                throw BusinessException.DateError.newInstance("判断标准期望值不能为空");
            }
            TextToSqlEvalAssertionEntity entity = new TextToSqlEvalAssertionEntity();
            entity.setEvalQuestionId(evalQuestionId);
            entity.setActualKey(actualKey);
            entity.setOperator(operator);
            entity.setExpectedValue(expectedValue);
            entity.setRequired(req.getRequired() == null || req.getRequired());
            entity.setWeight(req.getWeight() == null ? BigDecimal.ONE : req.getWeight());
            entity.setFailureType(normalizeBlank(req.getFailureType()));
            entity.setReferenceAnswer(normalizeBlank(req.getReferenceAnswer()));
            entity.setKeyPoints(normalizeBlank(req.getKeyPoints()));
            entity.setForbiddenPoints(normalizeBlank(req.getForbiddenPoints()));
            entity.setMinScore(req.getMinScore());
            entity.setSortOrder(i + 1);
            entity.setRemark(normalizeBlank(req.getRemark()));
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            result.add(entity);
        }
        return result;
    }

    private String normalizeStatus(String value) {
        String status = normalizeBlank(value);
        if (status == null) {
            return STATUS_DRAFT;
        }
        if (!SUPPORT_STATUS.contains(status)) {
            throw BusinessException.DateError.newInstance("评测样本状态不支持");
        }
        return status;
    }

    private String normalizeTarget(String value) {
        String target = normalizeBlank(value);
        if (target == null) {
            return TARGET_END_TO_END;
        }
        if (!optionValues(loadMetadata().getEvalTargets()).contains(target)) {
            throw BusinessException.DateError.newInstance("评测目标不支持");
        }
        return target;
    }

    private String normalizeCategory(String value) {
        String category = normalizeBlank(value);
        if (category == null) {
            return CATEGORY_NORMAL;
        }
        if (!optionValues(loadMetadata().getSampleCategories()).contains(category)) {
            throw BusinessException.DateError.newInstance("样本场景不支持");
        }
        return category;
    }

    private String inferEvalTarget(TextToSqlRunEntity run) {
        String errorType = normalizeBlank(run.getFeedbackErrorType());
        // 有人工错误类型时优先按错误类型推断评测目标，这样回归样本会落到最相关的能力环节。
        if ("BUSINESS".equals(errorType) || "ROUTE".equals(errorType)) {
            return "ROUTING";
        }
        if ("SKILL".equals(errorType)) {
            return "BUSINESS_SKILL";
        }
        if ("SQL".equals(errorType)) {
            return "SQL_GENERATION";
        }
        if ("CLARIFICATION".equals(errorType)) {
            return "CLARIFICATION";
        }
        if ("RESULT".equals(errorType) || "ANALYSIS".equals(errorType)) {
            return "RESULT_ANALYSIS";
        }
        String action = normalizeBlank(run.getSqlAction());
        // 没有反馈时，再根据本次 SQL action 推断它主要覆盖哪段链路。
        if ("need_metric".equals(action)) {
            return "METRIC_RETRIEVAL";
        }
        if ("need_schema".equals(action)) {
            return "TABLE_SCHEMA_RETRIEVAL";
        }
        if ("need_clarification".equals(action)) {
            return "CLARIFICATION";
        }
        if ("generate_sql".equals(action)) {
            return "SQL_GENERATION";
        }
        return TARGET_END_TO_END;
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            throw BusinessException.DateError.newInstance(message);
        }
        return normalized;
    }

    private String normalizeAssertionOperator(String value) {
        String operator = normalizeBlank(value);
        if (operator == null) {
            return OPERATOR_EQ;
        }
        Set<String> configOperators = optionValues(loadMetadata().getAssertionOperators());
        if (!ENGINE_SUPPORT_OPERATOR.contains(operator) || !configOperators.contains(operator)) {
            throw BusinessException.DateError.newInstance("判断方式不支持");
        }
        return operator;
    }

    private String normalizeAssertionKey(String value, String operator) {
        String actualKey = normalizeBlank(value);
        if (actualKey != null) {
            return actualKey;
        }
        if (OPERATOR_SEMANTIC.equals(operator)) {
            return "final_answer";
        }
        throw BusinessException.DateError.newInstance("判断标准期望Key不能为空");
    }

    private boolean requiresExpectedValue(String operator) {
        return !"EXISTS".equals(operator) && !"NOT_EMPTY".equals(operator);
    }

    private TextToSqlEvalMetadataRes loadMetadata() {
        try {
            String metadataJson = sysConfigService.getConfig(SysConfig.textToSqlEvalMetadata);
            TextToSqlEvalMetadataRes metadata = JSON.parseObject(metadataJson, TextToSqlEvalMetadataRes.class);
            validateMetadata(metadata);
            return metadata;
        } catch (Exception e) {
            // 实时配置可能被人工改错；回退默认值，避免样本页和保存动作被配置错误拖垮。
            return defaultMetadata();
        }
    }

    private void validateMetadata(TextToSqlEvalMetadataRes metadata) {
        if (metadata == null
                || isEmpty(metadata.getEvalTargets())
                || isEmpty(metadata.getSampleCategories())
                || isEmpty(metadata.getAssertionOperators())
                || isEmpty(metadata.getFailureTypes())) {
            throw BusinessException.DateError.newInstance("Text-to-SQL评测配置不完整");
        }
    }

    private TextToSqlEvalMetadataRes defaultMetadata() {
        TextToSqlEvalMetadataRes metadata = new TextToSqlEvalMetadataRes();
        metadata.setEvalTargets(List.of(
                option("端到端效果", TARGET_END_TO_END, "blue"),
                option("问题路由", "ROUTING", "purple"),
                option("业务规则/Skill", "BUSINESS_SKILL", "cyan"),
                option("指标检索", "METRIC_RETRIEVAL", "geekblue"),
                option("表结构检索", "TABLE_SCHEMA_RETRIEVAL", "gold"),
                option("SQL生成", "SQL_GENERATION", "green"),
                option("澄清判断", "CLARIFICATION", "orange"),
                option("结果分析", "RESULT_ANALYSIS", "magenta")
        ));
        metadata.setSampleCategories(List.of(
                option("正常样本", CATEGORY_NORMAL, "green"),
                option("边界样本", "BOUNDARY", "orange"),
                option("回归问题", "REGRESSION", "red"),
                option("歧义澄清", "AMBIGUOUS", "gold"),
                option("负向样本", "NEGATIVE", "default")
        ));
        metadata.setAssertionOperators(List.of(
                option("等于", OPERATOR_EQ, null),
                option("包含", "CONTAINS", null),
                option("不包含", "NOT_CONTAINS", null),
                option("存在", "EXISTS", null),
                option("不为空", "NOT_EMPTY", null),
                option("正则匹配", "REGEX", null),
                option("语义判断", OPERATOR_SEMANTIC, null)
        ));
        metadata.setFailureTypes(List.of(
                option("路由错误", "ROUTING_ERROR", null),
                option("表选择错误", "TABLE_SELECTION_ERROR", null),
                option("字段选择错误", "FIELD_SELECTION_ERROR", null),
                option("SQL动作错误", "SQL_ACTION_ERROR", null),
                option("SQL生成错误", "SQL_GENERATION_ERROR", null),
                option("澄清判断错误", "CLARIFICATION_ERROR", null),
                option("回答质量错误", "ANSWER_QUALITY_ERROR", null),
                option("State断言错误", "STATE_ASSERT_ERROR", null)
        ));
        return metadata;
    }

    private TextToSqlEvalOptionRes option(String label, String value, String color) {
        TextToSqlEvalOptionRes option = new TextToSqlEvalOptionRes();
        option.setLabel(label);
        option.setValue(value);
        option.setColor(color);
        return option;
    }

    private Set<String> optionValues(List<TextToSqlEvalOptionRes> options) {
        if (options == null || options.isEmpty()) {
            return Set.of();
        }
        return options.stream()
                .map(TextToSqlEvalOptionRes::getValue)
                .filter(value -> normalizeBlank(value) != null)
                .collect(Collectors.toSet());
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private List<TextToSqlEvalAssertionReq> buildAssertionsFromRun(TextToSqlRunEntity run) {
        List<TextToSqlEvalAssertionReq> assertions = new ArrayList<>();
        addStateAssertion(assertions, "business_id", "EQ", run.getBusinessId(), "ROUTING_ERROR");
        addStateAssertion(assertions, "business_name", "EQ", run.getBusinessName(), "ROUTING_ERROR");
        addStateAssertion(assertions, "question_type", "EQ", run.getQuestionType(), "ROUTING_ERROR");
        addStateAssertion(assertions, "action", "EQ", run.getSqlAction(), "SQL_ACTION_ERROR");
        addStateContainsAssertions(assertions, "used_tables", run.getUsedTables(), "TABLE_SELECTION_ERROR");
        addStateContainsAssertions(assertions, "used_fields", run.getUsedFields(), "FIELD_SELECTION_ERROR");
        if (run.getInterrupted() != null) {
            addStateAssertion(assertions, "interrupted", "EQ", String.valueOf(run.getInterrupted()), "CLARIFICATION_ERROR");
        }
        return assertions;
    }

    private void addStateAssertion(List<TextToSqlEvalAssertionReq> assertions, String key, String operator, String value, String failureType) {
        String expectedValue = normalizeBlank(value);
        if (expectedValue == null) {
            return;
        }
        TextToSqlEvalAssertionReq req = new TextToSqlEvalAssertionReq();
        req.setActualKey(key);
        req.setOperator(operator);
        req.setExpectedValue(expectedValue);
        req.setFailureType(failureType);
        req.setRequired(true);
        assertions.add(req);
    }

    private void addStateContainsAssertions(List<TextToSqlEvalAssertionReq> assertions, String key, String value, String failureType) {
        Object parsed = parseJsonValue(value);
        if (parsed instanceof List<?> list) {
            for (Object item : list) {
                addStateAssertion(assertions, key, "CONTAINS", item == null ? null : String.valueOf(item), failureType);
            }
            return;
        }
        addStateAssertion(assertions, key, "CONTAINS", parsed == null ? null : String.valueOf(parsed), failureType);
    }

    private Object parseJsonValue(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return null;
        }
        try {
            return JSON.parse(normalized);
        } catch (JSONException ex) {
            // 历史数据如果不是合法 JSON，也保留原始字符串，避免样本沉淀时丢信息。
            return normalized;
        }
    }

    private String firstNotBlank(String first, String second) {
        String firstValue = normalizeBlank(first);
        return firstValue == null ? normalizeBlank(second) : firstValue;
    }

    private String normalizeBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

