package cn.yanque.models.system.permission.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除权限响应")

/**
 * PermissionDeleteRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class PermissionDeleteRes {

    @Schema(description = "权限ID")
    private Long id;
}
