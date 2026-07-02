package cn.yanque.models.edu.campus.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除校区响应")

/**
 * CampusDeleteRes 响应结果对象。
 *
 * <p>用于把业务层处理后的数据整理成前端需要的展示结构。</p>
 */
public class CampusDeleteRes {

    @Schema(description = "校区ID")
    private Long id;
}
