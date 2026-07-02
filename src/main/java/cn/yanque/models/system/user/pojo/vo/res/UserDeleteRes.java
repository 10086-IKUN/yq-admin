package cn.yanque.models.system.user.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除用户响应")

/**
 * UserDeleteRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class UserDeleteRes {

    @Schema(description = "用户ID")
    private Long id;
}
