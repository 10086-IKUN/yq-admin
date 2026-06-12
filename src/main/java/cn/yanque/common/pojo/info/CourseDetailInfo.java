package cn.yanque.common.pojo.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailInfo {
    @Schema(description = "所属阶段")
    private String stageName;

    @Schema(description = "第几天课")
    private Integer dayNum;

    @Schema(description = "课程内容")
    private String courseContent;
}
