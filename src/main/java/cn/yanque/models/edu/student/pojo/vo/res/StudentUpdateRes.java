package cn.yanque.models.edu.student.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改学员响应")

/**
 * StudentUpdateRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class StudentUpdateRes {

    @Schema(description = "学员ID")
    private Long id;
}
