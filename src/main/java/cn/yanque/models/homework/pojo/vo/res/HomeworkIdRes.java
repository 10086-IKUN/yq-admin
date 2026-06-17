package cn.yanque.models.homework.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通用作业 ID 响应。
 *
 * <p>创建、更新、删除、关闭等接口只需要告诉前端操作的是哪条作业，
 * 所以统一返回这个轻量对象。</p>
 */
@Data
@AllArgsConstructor
@Schema(description = "通用作业ID响应")
public class HomeworkIdRes {
    /** 作业 ID。 */
    @Schema(description = "作业ID")
    private Long id;
}
