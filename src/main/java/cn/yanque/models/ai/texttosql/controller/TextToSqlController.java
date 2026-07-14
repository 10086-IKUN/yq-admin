package cn.yanque.models.ai.texttosql.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.models.ai.texttosql.biz.TextToSqlBiz;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlContinueReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlRouteReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/textToSql")
@RequirePermission("menu:text-to-sql")
@Tag(name = "TextToSqlController", description = "自然语言生成只读 SQL")
public class TextToSqlController {

    @Autowired
    private TextToSqlBiz textToSqlBiz;

    @PostMapping("/route")
    @Operation(description = "提交自然语言数据查询")
    public ApiResponse<TextToSqlRes> route(@Valid @RequestBody TextToSqlRouteReq req) {
        return ApiResponse.success(textToSqlBiz.route(req));
    }

    @PostMapping("/continue")
    @Operation(description = "补充查询信息并恢复生成流程")
    public ApiResponse<TextToSqlRes> continueQuestion(@Valid @RequestBody TextToSqlContinueReq req) {
        return ApiResponse.success(textToSqlBiz.continueQuestion(req));
    }
}
