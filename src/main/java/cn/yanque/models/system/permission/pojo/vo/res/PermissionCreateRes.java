package cn.yanque.models.system.permission.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建权限响应")

/**
 * PermissionCreateRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class PermissionCreateRes {

    @Schema(description = "权限ID")
    private Long id;
}
