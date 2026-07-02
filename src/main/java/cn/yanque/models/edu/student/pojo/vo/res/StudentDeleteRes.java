package cn.yanque.models.edu.student.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除学员响应")

/**
 * StudentDeleteRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class StudentDeleteRes {

    @Schema(description = "学员ID")
    private Long id;
}
