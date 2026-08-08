package cn.yanque.models.ai.texttosql.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.texttosql.biz.TextToSqlEvalRunBiz;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunClarifyReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunCreateReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunResultPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalRunRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalRunResultRes;
import cn.yanque.models.ai.texttosql.service.TextToSqlEvalRunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/textToSql/evalRuns")
@RequirePermission("menu:text-to-sql")
@Tag(name = "TextToSqlEvalRunController", description = "Text-to-SQL 评测任务")
public class TextToSqlEvalRunController {

    @Autowired
    private TextToSqlEvalRunService textToSqlEvalRunService;

    @Autowired
    private TextToSqlEvalRunBiz textToSqlEvalRunBiz;

    @GetMapping
    @Operation(description = "分页查询 Text-to-SQL 评测任务")
    public ApiResponse<PageResult<TextToSqlEvalRunRes>> page(@Valid @ModelAttribute TextToSqlEvalRunPageReq req) {
        return ApiResponse.success(textToSqlEvalRunService.page(req));
    }

    @GetMapping("/{id}")
    @Operation(description = "查询 Text-to-SQL 评测任务详情")
    public ApiResponse<TextToSqlEvalRunRes> detail(@Parameter(description = "评测任务ID") @PathVariable Long id) {
        return ApiResponse.success(textToSqlEvalRunService.detail(id));
    }

    @PostMapping
    @Operation(description = "新建并运行 Text-to-SQL 评测任务")
    public ApiResponse<IdRes> createAndRun(@Valid @RequestBody TextToSqlEvalRunCreateReq req, HttpServletRequest request) {
        return ApiResponse.success(textToSqlEvalRunBiz.createAndRun(req, (Long) request.getAttribute("userId")));
    }

    @GetMapping("/{id}/results")
    @Operation(description = "分页查询 Text-to-SQL 评测任务结果")
    public ApiResponse<PageResult<TextToSqlEvalRunResultRes>> resultPage(@Parameter(description = "评测任务ID") @PathVariable Long id,
                                                                         @Valid @ModelAttribute TextToSqlEvalRunResultPageReq req) {
        return ApiResponse.success(textToSqlEvalRunService.resultPage(id, req));
    }

    @PostMapping("/results/{resultId}/clarify")
    @Operation(description = "补充澄清并继续单条 Text-to-SQL 评测结果")
    public ApiResponse<TextToSqlEvalRunResultRes> clarifyResult(@Parameter(description = "评测结果ID") @PathVariable Long resultId,
                                                                @Valid @RequestBody TextToSqlEvalRunClarifyReq req) {
        return ApiResponse.success(textToSqlEvalRunBiz.clarifyResult(resultId, req));
    }
}
