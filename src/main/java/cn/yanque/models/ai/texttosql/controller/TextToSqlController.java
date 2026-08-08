package cn.yanque.models.ai.texttosql.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.models.ai.texttosql.biz.TextToSqlBiz;
import cn.yanque.models.ai.texttosql.pojo.dto.TextToSqlRouteDto;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlFeedbackReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlRunPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlFeedbackRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlRunRes;
import cn.yanque.models.ai.texttosql.service.TextToSqlFeedbackService;
import cn.yanque.models.ai.texttosql.service.TextToSqlRunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/textToSql")
@RequirePermission("menu:text-to-sql")
@Tag(name = "TextToSqlController", description = "自然语言生成只读 SQL")
public class TextToSqlController {

    @Autowired
    private TextToSqlBiz textToSqlBiz;

    @Autowired
    private TextToSqlRunService textToSqlRunService;

    @Autowired
    private TextToSqlFeedbackService textToSqlFeedbackService;

    @PostMapping("/route")
    @Operation(description = "提交自然语言数据查询")
    public ApiResponse<TextToSqlRouteDto.RouteRes> route(@Valid @RequestBody TextToSqlRouteDto.RouteReq req,
                                                         HttpServletRequest request) {
        return ApiResponse.success(textToSqlBiz.route(req, (Long) request.getAttribute("userId")));
    }

    @PostMapping("/continue")
    @Operation(description = "补充查询信息并恢复生成流程")
    public ApiResponse<TextToSqlRouteDto.RouteRes> continueQuestion(@Valid @RequestBody TextToSqlRouteDto.ContinueReq req) {
        return ApiResponse.success(textToSqlBiz.continueQuestion(req));
    }

    @PostMapping("/feedback")
    @Operation(description = "提交 Text-to-SQL 人工反馈")
    public ApiResponse<TextToSqlFeedbackRes> feedback(@Valid @RequestBody TextToSqlFeedbackReq req,
                                                       HttpServletRequest request) {
        return ApiResponse.success(textToSqlFeedbackService.create(req, (Long) request.getAttribute("userId")));
    }

    @GetMapping("/runs")
    @Operation(description = "分页查询 Text-to-SQL 运行记录")
    public ApiResponse<PageResult<TextToSqlRunRes>> pageRuns(@Valid @ModelAttribute TextToSqlRunPageReq req) {
        return ApiResponse.success(textToSqlRunService.page(req));
    }

    @GetMapping("/runs/{id}")
    @Operation(description = "查询 Text-to-SQL 运行记录详情")
    public ApiResponse<TextToSqlRunRes> runDetail(@Parameter(description = "运行记录ID") @PathVariable Long id) {
        return ApiResponse.success(textToSqlRunService.detail(id));
    }
}
