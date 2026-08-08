package cn.yanque.models.ai.texttosql.biz.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.texttosql.biz.TextToSqlBiz;
import cn.yanque.models.ai.texttosql.biz.TextToSqlEvalRunBiz;
import cn.yanque.models.ai.texttosql.pojo.dto.TextToSqlRouteDto;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalAssertionEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalQuestionEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalRunEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalRunResultEntity;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunClarifyReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunCreateReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalRunResultRes;
import cn.yanque.models.ai.texttosql.service.TextToSqlEvalQuestionService;
import cn.yanque.models.ai.texttosql.service.TextToSqlEvalRunService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Slf4j
@Component
public class TextToSqlEvalRunBizImpl implements TextToSqlEvalRunBiz {

    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_WAITING_CLARIFICATION = "WAITING_CLARIFICATION";
    private static final String RUN_SOURCE_EVAL = "EVAL";
    private static final String FAILURE_CLARIFICATION_REQUIRED = "CLARIFICATION_REQUIRED";
    private static final String FAILURE_CLARIFICATION_RUNNING = "CLARIFICATION_RUNNING";

    @Autowired
    private TextToSqlBiz textToSqlBiz;

    @Autowired
    private TextToSqlEvalQuestionService textToSqlEvalQuestionService;

    @Autowired
    private TextToSqlEvalRunService textToSqlEvalRunService;

    @Override
    public IdRes createAndRun(TextToSqlEvalRunCreateReq req, Long createdBy) {
        List<Long> ids = req.getEvalQuestionIds() == null ? Collections.emptyList() : req.getEvalQuestionIds();
        if (ids.isEmpty()) {
            throw BusinessException.DateError.newInstance("请选择评测样本");
        }
        // 只允许运行 ACTIVE 正式样本，避免候选样本或停用样本进入评测统计。
        List<TextToSqlEvalQuestionEntity> questions = textToSqlEvalQuestionService.selectActiveEntitiesByIds(ids);
        if (questions.isEmpty()) {
            throw BusinessException.DateError.newInstance("未找到可运行的正式样本");
        }

        Date now = new Date();
        TextToSqlEvalRunEntity run = new TextToSqlEvalRunEntity();
        // 任务主表先落 RUNNING 状态和样本范围，前端拿到任务ID后通过轮询看执行进度。
        run.setName(normalizeRequired(req.getName(), "评测任务名称不能为空"));
        run.setStatus(STATUS_RUNNING);
        run.setSampleCount(questions.size());
        run.setPassCount(0);
        run.setFailCount(0);
        run.setPassRate(BigDecimal.ZERO);
        run.setScopeJson(JSON.toJSONString(Map.of("evalQuestionIds", questions.stream().map(TextToSqlEvalQuestionEntity::getId).toList())));
        run.setStartedAt(now);
        run.setCreatedBy(createdBy);
        run.setCreatedAt(now);
        run.setUpdatedAt(now);
        textToSqlEvalRunService.insertRun(run);

        // 评测任务可能包含多条样本，每条样本都会真实跑完整 graph；这里异步执行，避免页面长时间阻塞。
        CompletableFuture.runAsync(() -> runEvalTask(run.getId(), questions, createdBy));
        return new IdRes(run.getId());
    }

    @Override
    public TextToSqlEvalRunResultRes clarifyResult(Long resultId, TextToSqlEvalRunClarifyReq req) {
        TextToSqlEvalRunResultEntity existed = textToSqlEvalRunService.selectResultEntityById(resultId);
        if (existed == null) {
            throw BusinessException.DateError.newInstance("评测结果不存在");
        }
        if (!FAILURE_CLARIFICATION_REQUIRED.equals(existed.getFailureType())) {
            throw BusinessException.DateError.newInstance("当前评测结果不需要补充澄清");
        }
        if (normalizeBlank(existed.getConversationId()) == null) {
            throw BusinessException.DateError.newInstance("评测结果缺少会话ID，不能继续澄清");
        }
        // 补充澄清也可能跑完整 graph 和 SQL 执行，先标记为执行中并立即返回，避免页面等待。
        textToSqlEvalRunService.markResultClarificationRunning(
                existed.getId(),
                FAILURE_CLARIFICATION_RUNNING,
                "澄清补充已提交，正在继续评测"
        );
        refreshEvalRunSummary(existed.getEvalRunId());

        CompletableFuture.runAsync(() -> runClarifyTask(existed, req.getUserAnswer()));
        return buildResultRes(textToSqlEvalRunService.selectResultEntityById(resultId));
    }

    private void runClarifyTask(TextToSqlEvalRunResultEntity existed, String userAnswer) {
        long started = System.currentTimeMillis();
        TextToSqlEvalRunResultEntity update = new TextToSqlEvalRunResultEntity();
        update.setId(existed.getId());
        update.setConversationId(existed.getConversationId());
        try {
            // 待澄清结果必须沿用第一轮评测的 conversationId，才能接回同一个 LangGraph 会话继续执行。
            TextToSqlRouteDto.ContinueReq continueReq = new TextToSqlRouteDto.ContinueReq();
            continueReq.setConversationId(existed.getConversationId());
            continueReq.setUserAnswer(userAnswer);
            TextToSqlRouteDto.RouteRes routeRes = textToSqlBiz.continueQuestion(continueReq);
            Map<String, Object> actualState = buildActualState(routeRes);

            update.setActualStateJson(JSON.toJSONString(actualState));
            update.setActualSql(firstNotBlank(routeRes.getExecutedSql(), routeRes.getSql()));
            update.setActualAnswer(routeRes.getFinalAnswer());
            update.setExecutionError(routeRes.getExecutionError());
            update.setDurationMs((existed.getDurationMs() == null ? 0 : existed.getDurationMs()) + System.currentTimeMillis() - started);

            List<TextToSqlEvalAssertionEntity> assertions = textToSqlEvalQuestionService.selectAssertionEntitiesByQuestionId(existed.getEvalQuestionId());
            JudgeResult judgeResult = judgeCompletion(assertions, actualState, routeRes);
            update.setPassed(judgeResult.passed());
            update.setFailureType(judgeResult.failureType());
            update.setFailureReason(judgeResult.failureReason());
        } catch (RuntimeException ex) {
            log.warn("Text-to-SQL 评测澄清继续执行失败，resultId={}", existed.getId(), ex);
            textToSqlEvalRunService.markResultClarificationRunError(
                    existed.getId(),
                    "RUN_ERROR",
                    ex.getMessage(),
                    ex.getMessage(),
                    (existed.getDurationMs() == null ? 0 : existed.getDurationMs()) + System.currentTimeMillis() - started
            );
            refreshEvalRunSummary(existed.getEvalRunId());
            return;
        }
        textToSqlEvalRunService.updateResultById(update);
        // 单条结果继续评测后，需要重新汇总任务状态；所有澄清都处理完才算 COMPLETED。
        refreshEvalRunSummary(existed.getEvalRunId());
    }

    private void runEvalTask(Long evalRunId, List<TextToSqlEvalQuestionEntity> questions, Long createdBy) {
        try {
            // 后台线程逐条运行样本。当前设计是串行执行，避免并发请求打满 Python 服务或数据库。
            RunSummary summary = executeQuestions(evalRunId, questions, createdBy);
            TextToSqlEvalRunEntity update = new TextToSqlEvalRunEntity();
            update.setId(evalRunId);
            update.setStatus(summary.waitingClarificationCount() > 0 ? STATUS_WAITING_CLARIFICATION : STATUS_COMPLETED);
            update.setPassCount(summary.passCount());
            update.setFailCount(summary.failCount());
            update.setPassRate(calculatePassRate(summary.passCount(), questions.size()));
            update.setSummaryJson(JSON.toJSONString(summary.failureTypeCounts()));
            update.setFinishedAt(new Date());
            update.setUpdatedAt(new Date());
            textToSqlEvalRunService.updateRunById(update);
        } catch (RuntimeException ex) {
            log.warn("Text-to-SQL 评测任务执行失败，evalRunId={}", evalRunId, ex);
            // 任务级异常才标记 FAILED；单条样本运行异常会落到该样本结果的 RUN_ERROR。
            TextToSqlEvalRunEntity update = new TextToSqlEvalRunEntity();
            update.setId(evalRunId);
            update.setStatus(STATUS_FAILED);
            update.setErrorMessage(ex.getMessage());
            update.setFinishedAt(new Date());
            update.setUpdatedAt(new Date());
            textToSqlEvalRunService.updateRunById(update);
        }
    }

    // 根据子任务的状态更新总任务状态
    private void refreshEvalRunSummary(Long evalRunId) {
        // 任务主表不单独保存“当前应该是什么状态”的业务判断；
        // 每次澄清开始或结束后，都从结果明细重新汇总，避免多条澄清并行/串行时状态被写乱。
        List<TextToSqlEvalRunResultEntity> results = textToSqlEvalRunService.selectResultEntitiesByEvalRunId(evalRunId);
        EvalRunResultSummary summary = summarizeResults(results);
        String nextStatus = resolveEvalRunStatus(summary);
        boolean running = STATUS_RUNNING.equals(nextStatus);

        TextToSqlEvalRunEntity update = new TextToSqlEvalRunEntity();
        update.setId(evalRunId);
        update.setStatus(nextStatus);
        update.setSampleCount(results.size());
        update.setPassCount(summary.passCount());
        update.setFailCount(summary.failCount());
        update.setPassRate(calculatePassRate(summary.passCount(), results.size()));
        update.setSummaryJson(JSON.toJSONString(summary.failureTypeCounts()));
        // RUNNING 表示还有异步澄清没有结束，完成时间必须清空；待澄清和完成都表示当前批次已停住。
        update.setFinishedAt(running ? null : new Date());
        update.setUpdatedAt(new Date());
        textToSqlEvalRunService.updateRunSummaryById(update, running);
    }

    private EvalRunResultSummary summarizeResults(List<TextToSqlEvalRunResultEntity> results) {
        int passCount = 0;
        int failCount = 0;
        int waitingClarificationCount = 0;
        int runningClarificationCount = 0;
        Map<String, Integer> failureTypeCounts = new LinkedHashMap<>();

        for (TextToSqlEvalRunResultEntity result : results) {
            if (Boolean.TRUE.equals(result.getPassed())) {
                passCount++;
                continue;
            }

            failCount++;
            String failureType = result.getFailureType() == null ? "UNKNOWN" : result.getFailureType();
            failureTypeCounts.put(failureType, failureTypeCounts.getOrDefault(failureType, 0) + 1);

            // CLARIFICATION_REQUIRED：这条结果停在澄清点，等用户补充回答。
            if (FAILURE_CLARIFICATION_REQUIRED.equals(failureType)) {
                waitingClarificationCount++;
            }
            // CLARIFICATION_RUNNING：用户已经补充回答，后台异步 continue 还没跑完。
            if (FAILURE_CLARIFICATION_RUNNING.equals(failureType)) {
                runningClarificationCount++;
            }
        }

        return new EvalRunResultSummary(passCount, failCount, waitingClarificationCount, runningClarificationCount, failureTypeCounts);
    }

    private String resolveEvalRunStatus(EvalRunResultSummary summary) {
        // 优先级 1：只要有任意澄清正在异步继续，整个任务就是 RUNNING。
        if (summary.runningClarificationCount() > 0) {
            return STATUS_RUNNING;
        }
        // 优先级 2：没有运行中的澄清，但仍有结果等待澄清，任务停在 WAITING_CLARIFICATION。
        if (summary.waitingClarificationCount() > 0) {
            return STATUS_WAITING_CLARIFICATION;
        }
        // 优先级 3：没有运行中、也没有待澄清，说明当前评测任务已经完成；普通失败仍属于 COMPLETED。
        return STATUS_COMPLETED;
    }

    private RunSummary executeQuestions(Long evalRunId, List<TextToSqlEvalQuestionEntity> questions, Long createdBy) {
        int passCount = 0;
        int failCount = 0;
        int waitingClarificationCount = 0;
        Map<String, Integer> failureTypeCounts = new LinkedHashMap<>();
        for (TextToSqlEvalQuestionEntity question : questions) {
            // 每条样本独立生成 conversationId，独立保存运行记录和评测结果。
            TextToSqlEvalRunResultEntity result = runOneQuestion(evalRunId, question, createdBy);
            if (Boolean.TRUE.equals(result.getPassed())) {
                passCount++;
            } else {
                failCount++;
                String failureType = result.getFailureType() == null ? "UNKNOWN" : result.getFailureType();
                failureTypeCounts.put(failureType, failureTypeCounts.getOrDefault(failureType, 0) + 1);
                if (FAILURE_CLARIFICATION_REQUIRED.equals(failureType)) {
                    waitingClarificationCount++;
                }
            }
            textToSqlEvalRunService.insertResult(result);
        }
        return new RunSummary(passCount, failCount, waitingClarificationCount, failureTypeCounts);
    }

    private TextToSqlEvalRunResultEntity runOneQuestion(Long evalRunId, TextToSqlEvalQuestionEntity question, Long createdBy) {
        long started = System.currentTimeMillis();
        // 评测运行使用独立 conversationId，并在运行记录中标记为 EVAL，避免污染真实用户运行记录。
        String conversationId = "eval-" + evalRunId + "-" + question.getId() + "-" + UUID.randomUUID();
        TextToSqlEvalRunResultEntity result = new TextToSqlEvalRunResultEntity();
        result.setEvalRunId(evalRunId);
        result.setEvalQuestionId(question.getId());
        result.setQuestion(question.getQuestion());
        result.setBusinessId(question.getBusinessId());
        result.setEvalTarget(question.getEvalTarget());
        result.setSampleCategory(question.getSampleCategory());
        result.setConversationId(conversationId);
        result.setCreatedAt(new Date());
        try {
            // 第一轮评测等价于真实用户提问，只是运行记录来源标记为 EVAL。
            TextToSqlRouteDto.RouteReq routeReq = new TextToSqlRouteDto.RouteReq();
            routeReq.setUserQuestion(question.getQuestion());
            routeReq.setConversationId(conversationId);
            TextToSqlRouteDto.RouteRes routeRes = textToSqlBiz.route(routeReq, createdBy, RUN_SOURCE_EVAL);
            Map<String, Object> actualState = buildActualState(routeRes);
            result.setActualStateJson(JSON.toJSONString(actualState));
            result.setActualSql(firstNotBlank(routeRes.getExecutedSql(), routeRes.getSql()));
            result.setActualAnswer(routeRes.getFinalAnswer());
            result.setExecutionError(routeRes.getExecutionError());

            List<TextToSqlEvalAssertionEntity> assertions = textToSqlEvalQuestionService.selectAssertionEntitiesByQuestionId(question.getId());
            JudgeResult judgeResult = judgeCompletion(assertions, actualState, routeRes);
            result.setPassed(judgeResult.passed());
            result.setFailureType(judgeResult.failureType());
            result.setFailureReason(judgeResult.failureReason());
        } catch (Exception ex) {
            // 单条样本失败不能中断整个任务；记录 RUN_ERROR 后继续跑下一条。
            result.setPassed(false);
            result.setFailureType("RUN_ERROR");
            result.setFailureReason(ex.getMessage());
            result.setExecutionError(ex.getMessage());
        } finally {
            result.setDurationMs(System.currentTimeMillis() - started);
        }
        return result;
    }

    private JudgeResult judgeCompletion(List<TextToSqlEvalAssertionEntity> assertions, Map<String, Object> actualState, TextToSqlRouteDto.RouteRes routeRes) {
        // 进入澄清态说明这条样本还没完成最终评测，需要人工补充后再 continue。
        if (Boolean.TRUE.equals(routeRes.getInterrupted()) || "need_clarification".equals(routeRes.getAction())) {
            return new JudgeResult(false, FAILURE_CLARIFICATION_REQUIRED, "本轮运行进入澄清态，需要补充回答后才算完成评测");
        }
        return judge(assertions, actualState, routeRes);
    }

    private JudgeResult judge(List<TextToSqlEvalAssertionEntity> assertions, Map<String, Object> actualState, TextToSqlRouteDto.RouteRes routeRes) {
        String executionError = normalizeBlank(routeRes.getExecutionError());
        if (executionError != null) {
            return new JudgeResult(false, "EXECUTION_ERROR", executionError);
        }
        if (assertions == null || assertions.isEmpty()) {
            return new JudgeResult(true, null, null);
        }
        for (TextToSqlEvalAssertionEntity assertion : assertions) {
            AssertionCheckResult checkResult = checkAssertion(assertion, actualState, routeRes);
            if (!checkResult.passed() && Boolean.TRUE.equals(assertion.getRequired())) {
                return new JudgeResult(false, firstNotBlank(assertion.getFailureType(), failureTypeForKey(assertion.getActualKey())),
                        checkResult.failureReason());
            }
        }
        return new JudgeResult(true, null, null);
    }

    private AssertionCheckResult checkAssertion(TextToSqlEvalAssertionEntity assertion, Map<String, Object> actualState, TextToSqlRouteDto.RouteRes routeRes) {
        Object actualValue = resolveActualValue(assertion, actualState, routeRes);
        String operator = normalizeBlank(assertion.getOperator());
        String expectedValue = assertion.getExpectedValue();
        boolean passed = switch (operator == null ? "EQ" : operator) {
            case "EXISTS" -> actualValue != null;
            case "NOT_EMPTY" -> isNotEmpty(actualValue);
            case "CONTAINS" -> containsValue(actualValue, expectedValue);
            case "NOT_CONTAINS" -> !containsValue(actualValue, expectedValue);
            case "REGEX" -> matchesRegex(actualValue, expectedValue);
            case "SEMANTIC" -> semanticFallbackPassed(actualValue, assertion);
            case "EQ" -> matchesExpected(expectedValue, actualValue);
            default -> false;
        };
        if (passed) {
            return new AssertionCheckResult(true, null);
        }
        return new AssertionCheckResult(false,
                assertion.getActualKey() + " " + operator + " 期望 " + JSON.toJSONString(expectedValue)
                        + "，实际 " + JSON.toJSONString(actualValue));
    }

    private Object resolveActualValue(TextToSqlEvalAssertionEntity assertion, Map<String, Object> actualState, TextToSqlRouteDto.RouteRes routeRes) {
        String actualKey = normalizeBlank(assertion.getActualKey());
        return actualState.get(actualKey);
    }

    private Map<String, Object> buildActualState(TextToSqlRouteDto.RouteRes routeRes) {
        if (routeRes.getStateSnapshot() != null) {
            return new LinkedHashMap<>(routeRes.getStateSnapshot());
        }
        return new LinkedHashMap<>();
    }

    private boolean matchesExpected(Object expectedValue, Object actualValue) {
        if (expectedValue == null) {
            return actualValue == null;
        }
        if (expectedValue instanceof String expectedString) {
            Boolean expectedBoolean = parseBoolean(expectedString);
            if (expectedBoolean != null && actualValue instanceof Boolean) {
                return Objects.equals(expectedBoolean, actualValue);
            }
        }
        if (expectedValue instanceof List<?> expectedList) {
            List<?> actualList = toList(actualValue);
            // 数组字段采用包含判断：实际使用的表/字段可以更多，但必须包含期望项。
            return actualList != null && actualList.containsAll(expectedList);
        }
        if (expectedValue instanceof Map<?, ?> expectedMap) {
            if (!(actualValue instanceof Map<?, ?> actualMap)) {
                return false;
            }
            for (Map.Entry<?, ?> entry : expectedMap.entrySet()) {
                if (!matchesExpected(entry.getValue(), actualMap.get(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }
        if (expectedValue instanceof Boolean expectedBoolean) {
            return Objects.equals(expectedBoolean, toBoolean(actualValue));
        }
        if (expectedValue instanceof Number) {
            return Objects.equals(String.valueOf(expectedValue), String.valueOf(actualValue));
        }
        return Objects.equals(String.valueOf(expectedValue), actualValue == null ? null : String.valueOf(actualValue));
    }

    private boolean containsValue(Object actualValue, String expectedValue) {
        String expected = normalizeBlank(expectedValue);
        if (expected == null) {
            return false;
        }
        List<?> actualList = toList(actualValue);
        if (actualList != null) {
            return actualList.stream().map(String::valueOf).anyMatch(expected::equals);
        }
        String actual = normalizeBlank(actualValue == null ? null : String.valueOf(actualValue));
        return actual != null && actual.contains(expected);
    }

    private boolean isNotEmpty(Object actualValue) {
        if (actualValue instanceof List<?> list) {
            return !list.isEmpty();
        }
        return normalizeBlank(actualValue == null ? null : String.valueOf(actualValue)) != null;
    }

    private boolean matchesRegex(Object actualValue, String expectedValue) {
        String actual = normalizeBlank(actualValue == null ? null : String.valueOf(actualValue));
        String expected = normalizeBlank(expectedValue);
        if (actual == null || expected == null) {
            return false;
        }
        try {
            return Pattern.compile(expected, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(actual).find();
        } catch (PatternSyntaxException ex) {
            throw BusinessException.DateError.newInstance("判断标准正则表达式不合法");
        }
    }

    private boolean semanticFallbackPassed(Object actualValue, TextToSqlEvalAssertionEntity assertion) {
        String actual = normalizeBlank(actualValue == null ? null : String.valueOf(actualValue));
        if (actual == null) {
            return false;
        }
        // 当前先用“关键要点逐条包含”兜底；后续接入 LLM judge 后，这里会替换为语义评分。
        List<String> keyPoints = splitLines(assertion.getKeyPoints());
        if (!keyPoints.isEmpty()) {
            return keyPoints.stream().allMatch(actual::contains);
        }
        String expected = firstNotBlank(assertion.getExpectedValue(), assertion.getReferenceAnswer());
        return expected == null || actual.contains(expected);
    }

    private List<String> splitLines(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return List.of();
        }
        return normalized.lines().map(String::trim).filter(line -> !line.isEmpty()).toList();
    }

    private Boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        return null;
    }

    private List<?> toList(Object value) {
        if (value instanceof List<?> list) {
            return list;
        }
        String normalized = normalizeBlank(value == null ? null : String.valueOf(value));
        if (normalized == null) {
            return null;
        }
        try {
            Object parsed = JSON.parse(normalized);
            return parsed instanceof List<?> list ? list : null;
        } catch (JSONException ex) {
            List<String> values = new ArrayList<>();
            values.add(normalized);
            return values;
        }
    }

    private Boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value == null) {
            return null;
        }
        return Boolean.valueOf(String.valueOf(value));
    }

    private String failureTypeForKey(String key) {
        // 根据失败字段归因，页面会按 failureType 做失败原因分布统计。
        return switch (key) {
            case "business_id", "business_name", "question_type" -> "ROUTING_ERROR";
            case "used_tables" -> "TABLE_SELECTION_ERROR";
            case "used_fields" -> "FIELD_SELECTION_ERROR";
            case "action" -> "SQL_ACTION_ERROR";
            case "interrupted", "missing_info" -> "CLARIFICATION_ERROR";
            case "sql", "executed_sql" -> "SQL_GENERATION_ERROR";
            default -> "STATE_ASSERT_ERROR";
        };
    }

    private BigDecimal calculatePassRate(int passCount, int sampleCount) {
        if (sampleCount == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(passCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(sampleCount), 2, RoundingMode.HALF_UP);
    }

    private TextToSqlEvalRunResultRes buildResultRes(TextToSqlEvalRunResultEntity entity) {
        TextToSqlEvalRunResultRes res = new TextToSqlEvalRunResultRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            throw BusinessException.DateError.newInstance(message);
        }
        return normalized;
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

    private record JudgeResult(boolean passed, String failureType, String failureReason) {
    }

    private record AssertionCheckResult(boolean passed, String failureReason) {
    }

    private record RunSummary(int passCount, int failCount, int waitingClarificationCount, Map<String, Integer> failureTypeCounts) {
    }

    private record EvalRunResultSummary(int passCount, int failCount, int waitingClarificationCount,
                                        int runningClarificationCount, Map<String, Integer> failureTypeCounts) {
    }
}

