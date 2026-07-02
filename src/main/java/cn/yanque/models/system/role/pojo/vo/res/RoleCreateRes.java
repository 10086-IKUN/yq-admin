package cn.yanque.models.system.role.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建角色响应")

/**
 * RoleCreateRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class RoleCreateRes {

    @Schema(description = "角色ID")
    private Long id;
}
