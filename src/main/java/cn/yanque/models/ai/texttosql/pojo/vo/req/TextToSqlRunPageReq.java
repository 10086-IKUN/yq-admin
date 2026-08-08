package cn.yanque.models.ai.texttosql.pojo.vo.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TextToSqlRunPageReq {

    private String keyword;

    private String conversationId;

    private String sourceType;

    private String questionType;

    private String businessId;

    private String status;

    private String sqlAction;

    private Boolean interrupted;

    private String feedbackResult;

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 200, message = "每页条数不能大于200")
    private Integer pageSize = 10;
}

