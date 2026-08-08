package cn.yanque.models.ai.texttosql.pojo.vo.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TextToSqlRunStepRes {

    private Long id;
    private Long runId;
    private String conversationId;
    private Integer stepIndex;
    private String nodeName;
    private String source;
    private Integer langGraphStep;
    private String nextNodes;
    private String stateSnapshotJson;
    private String checkpointCreatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date savedAt;
}

