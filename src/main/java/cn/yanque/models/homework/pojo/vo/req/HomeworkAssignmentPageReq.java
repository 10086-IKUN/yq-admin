package cn.yanque.models.homework.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作业分页查询请求。
 *
 * <p>管理端作业列表使用。
 * 查询条件都允许为空，为空时表示不按该条件过滤。</p>
 */
@Data
@Schema(description = "作业分页查询请求")
public class HomeworkAssignmentPageReq {
    /** 关键字，通常匹配作业标题或说明。 */
    @Schema(description = "关键字")
    private String keyword;

    /** 班级 ID。 */
    @Schema(description = "班级ID")
    private Long classId;

    /** 课程 ID。 */
    @Schema(description = "课程ID")
    private Long courseId;

    /** 作业状态：PUBLISHED 已发布，CLOSED 已关闭。 */
    @Schema(description = "作业状态", allowableValues = {"PUBLISHED", "CLOSED"})
    private String status;

    /** 答案发布状态：UNPUBLISHED 未发布，PUBLISHED 已发布。 */
    @Schema(description = "答案发布状态", allowableValues = {"UNPUBLISHED", "PUBLISHED"})
    private String answerPublishStatus;

    /** 当前页码，默认第一页。 */
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页条数，默认 10 条。 */
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
}
