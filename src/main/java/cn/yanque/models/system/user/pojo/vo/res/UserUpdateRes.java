package cn.yanque.models.system.user.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改用户响应")

/**
 * UserUpdateRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class UserUpdateRes {

    @Schema(description = "用户ID")
    private Long id;
}
