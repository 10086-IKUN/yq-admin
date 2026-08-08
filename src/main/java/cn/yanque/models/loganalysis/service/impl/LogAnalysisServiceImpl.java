package cn.yanque.models.loganalysis.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.config.VolcengineTlsProperties;
import cn.yanque.models.loganalysis.pojo.vo.req.LogSearchReq;
import cn.yanque.models.loganalysis.pojo.vo.res.LogRecordRes;
import cn.yanque.models.loganalysis.pojo.vo.res.LogSearchRes;
import cn.yanque.models.loganalysis.service.LogAnalysisService;
import cn.yanque.models.system.config.service.SysConfig;
import cn.yanque.models.system.config.service.SysConfigService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.volcengine.model.tls.ClientBuilder;
import com.volcengine.model.tls.ClientConfig;
import com.volcengine.model.tls.request.SearchLogsRequest;
import com.volcengine.model.tls.response.SearchLogsResponseV2;
import com.volcengine.service.tls.TLSLogClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogAnalysisServiceImpl implements LogAnalysisService {

    private final VolcengineTlsProperties tlsProperties;
    private final SysConfigService sysConfigService;

    @Override
    public LogSearchRes search(LogSearchReq request) {
        validateConfig();

        SearchLogsRequest searchRequest = new SearchLogsRequest();
        searchRequest.setTopicId(resolveTopicId());
        searchRequest.setQuery(StringUtils.hasText(request.getQuery()) ? request.getQuery().trim() : "*");
        searchRequest.setStartTime(toEpochSeconds(request.getStartTime()));
        searchRequest.setEndTime(toEpochSeconds(request.getEndTime()));
        searchRequest.setLimit(request.getLimit());
        if (searchRequest.getEndTime() < searchRequest.getStartTime()) {
            throw new BusinessException(400, "结束时间不能早于开始时间");
        }

        TLSLogClient client = buildClient();
        try {
            SearchLogsResponseV2 response = client.searchLogsV2(searchRequest);
            return buildResponse(searchRequest, response);
        } catch (Exception exception) {
            log.error("火山日志检索失败: topicId={}, query={}",
                    searchRequest.getTopicId(), searchRequest.getQuery(), exception);
            throw new BusinessException(502, "查询火山日志失败: " + safeMessage(exception));
        } finally {
            try {
                client.close();
            } catch (Exception exception) {
                log.warn("关闭火山日志客户端失败", exception);
            }
        }
    }

    private TLSLogClient buildClient() {
        ClientConfig config = new ClientConfig(
                tlsProperties.getEndpoint(),
                tlsProperties.getRegion(),
                tlsProperties.getAccessKeyId(),
                tlsProperties.getAccessKeySecret()
        );
        try {
            TLSLogClient client = ClientBuilder.newClient(config);
            client.setTimeout(tlsProperties.getConnectTimeoutMs(), tlsProperties.getReadTimeoutMs());
            return client;
        } catch (Exception exception) {
            throw new BusinessException(500, "初始化火山日志客户端失败: " + safeMessage(exception));
        }
    }

    private LogSearchRes buildResponse(SearchLogsRequest request, SearchLogsResponseV2 response) {
        LogSearchRes result = new LogSearchRes();
        result.setTopicId(request.getTopicId());
        result.setQuery(request.getQuery());
        result.setStartTime(request.getStartTime());
        result.setEndTime(request.getEndTime());
        result.setLimit(request.getLimit());
        result.setIsAnalysis(response.isAnalysis());
        result.setCount((long) response.getCount());
        result.setHitCount((long) response.getHitCount());
        result.setResultStatus(response.getResultStatus());
        result.setListOver(response.isListOver());
        result.setContext(response.getContext());
        result.setLogs(parseLogs(response.getLogs()));
        return result;
    }

    private List<LogRecordRes> parseLogs(List<Map<String, Object>> logs) {
        if (logs == null || logs.isEmpty()) {
            return List.of();
        }
        return logs.stream()
                .filter(Objects::nonNull)
                .map(item -> convertLogRecord((JSONObject) JSON.toJSON(item)))
                .toList();
    }

    private LogRecordRes convertLogRecord(JSONObject source) {
        LogRecordRes record = new LogRecordRes();
        record.setTime(firstNonBlank(
                source.getString("time"),
                source.getString("__time__"),
                source.getString("timestamp")
        ));
        record.setContent(firstNonBlank(
                source.getString("__content__"),
                source.getString("content"),
                JSON.toJSONString(source)
        ));
        return record;
    }

    private String resolveTopicId() {
        String topicId = sysConfigService.getConfig(SysConfig.volcengineTlsDefaultTopicId);
        if (!StringUtils.hasText(topicId)) {
            throw new BusinessException(400, "请先在系统配置中设置 volcengine.tls.default.topic.id");
        }
        return topicId.trim();
    }

    private long toEpochSeconds(Date value) {
        if (value == null) {
            throw new BusinessException(400, "查询时间不能为空");
        }
        return value.getTime() / 1000;
    }

    private void validateConfig() {
        if (!StringUtils.hasText(tlsProperties.getEndpoint())
                || !StringUtils.hasText(tlsProperties.getAccessKeyId())
                || !StringUtils.hasText(tlsProperties.getAccessKeySecret())) {
            throw new BusinessException(500, "火山日志配置不完整，请检查 endpoint、AK 和 SK");
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
    }
}
