package cn.yanque.models.ai.texttosql.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextToSqlRouteDto {

    @Data
    public static class RouteReq {
        @NotBlank(message = "问题不能为空")
        private String userQuestion;
        private String conversationId;
    }

    @Data
    public static class ContinueReq {
        @NotBlank(message = "会话ID不能为空")
        private String conversationId;
        @NotBlank(message = "补充内容不能为空")
        private String userAnswer;
    }

    @Data
    public static class RouteRes {
        private String questionType;
        private String routeReason;
        private String businessId;
        private String businessName;
        private String finalAnswer;
        private String action;
        private String sql;
        private String reason;
        private List<String> missingInfo = new ArrayList<>();
        private List<String> usedTables = new ArrayList<>();
        private List<String> usedFields = new ArrayList<>();
        private List<String> columns = new ArrayList<>();
        private List<Map<String, Object>> rows = new ArrayList<>();
        private Integer rowCount;
        private String executedSql;
        private String executionError;
        private Analysis analysis;
        private String conversationId;
        private Boolean interrupted = false;
        private Map<String, Object> stateSnapshot;
        private List<Map<String, Object>> stateHistory = new ArrayList<>();
    }

    @Data
    public static class Analysis {
        private String summary;
        private List<String> findings = new ArrayList<>();
        private List<String> basis = new ArrayList<>();
        private Chart chart;
        private List<String> warnings = new ArrayList<>();
    }

    @Data
    public static class Chart {
        private String type;
        private String title;
        private String reason;
        private String xField;
        private List<String> yFields = new ArrayList<>();
        private String categoryField;
        private List<Map<String, Object>> data = new ArrayList<>();
    }
}

