package cn.yanque.models.exam.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 题库分页查询参数。
 */
@Data
@Schema(description = "题库分页查询参数")
public class ExamQuestionPageReq {

    /** 题干关键词。 */
    @Schema(description = "题干关键词")
    private String keyword;

    /** 题型筛选。 */
    @Schema(description = "题型")
    private String questionType;

    /** 状态筛选。 */
    @Schema(description = "状态")
    private Integer status;

    /** 页码。 */
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页数量。 */
    @Schema(description = "每页数量", defaultValue = "10")
    private Integer pageSize = 10;
}
