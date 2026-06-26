package cn.yanque.models.exam.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 试卷分页查询参数。
 */
@Data
@Schema(description = "试卷分页查询参数")
public class ExamPaperPageReq {

    /** 试卷名称关键词。 */
    @Schema(description = "试卷名称关键词")
    private String keyword;

    /** 试卷状态。 */
    @Schema(description = "试卷状态")
    private String status;

    /** 页码。 */
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页数量。 */
    @Schema(description = "每页数量", defaultValue = "10")
    private Integer pageSize = 10;
}
