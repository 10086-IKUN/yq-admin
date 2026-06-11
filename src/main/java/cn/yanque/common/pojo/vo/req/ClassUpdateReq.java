package cn.yanque.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改班级请求")
public class ClassUpdateReq extends ClassCreateReq {

    @Schema(description = "班级ID")
    private Long id;
}
