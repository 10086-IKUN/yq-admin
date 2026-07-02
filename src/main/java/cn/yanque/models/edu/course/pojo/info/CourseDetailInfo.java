package cn.yanque.models.edu.course.pojo.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

/**
 * CourseDetailInfo 聚合信息对象。
 *
 * <p>用于承载跨表组装后的业务信息，避免控制层直接拼装零散字段。</p>
 */
public class CourseDetailInfo {
    @Schema(description = "所属阶段")
    private String stageName;

    @Schema(description = "第几天课")
    private Integer dayNum;

    @Schema(description = "课程内容")
    private String courseContent;
}
