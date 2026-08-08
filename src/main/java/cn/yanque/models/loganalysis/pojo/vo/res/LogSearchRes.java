package cn.yanque.models.loganalysis.pojo.vo.res;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LogSearchRes {

    private String topicId;
    private String query;
    private Long startTime;
    private Long endTime;
    private Integer limit;
    private Boolean isAnalysis;
    private Long count;
    private Long hitCount;
    private String resultStatus;
    private Boolean listOver;
    private String context;
    private List<LogRecordRes> logs = new ArrayList<>();
}
