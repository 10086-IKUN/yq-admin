package cn.yanque.models.ai.texttosql.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalQuestionPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalQuestionSaveReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalQuestionUpdateReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalMetadataRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalQuestionRes;
import cn.yanque.models.ai.texttosql.service.TextToSqlEvalQuestionService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/textToSql/evalQuestions")
@RequirePermission("menu:text-to-sql")
@Tag(name = "TextToSqlEvalQuestionController", description = "Text-to-SQL 评测样本")
public class TextToSqlEvalQuestionController {

    @Autowired
    private TextToSqlEvalQuestionService textToSqlEvalQuestionService;

    @GetMapping
    @Operation(description = "分页查询 Text-to-SQL 评测样本")
    public ApiResponse<PageResult<TextToSqlEvalQuestionRes>> page(@Valid @ModelAttribute TextToSqlEvalQuestionPageReq req) {
        return ApiResponse.success(textToSqlEvalQuestionService.page(req));
    }

    @GetMapping("/metadata")
    @Operation(description = "查询 Text-to-SQL 评测样本页面元数据")
    public ApiResponse<TextToSqlEvalMetadataRes> metadata() {
        return ApiResponse.success(textToSqlEvalQuestionService.metadata());
    }

    @GetMapping("/{id}")
    @Operation(description = "查询 Text-to-SQL 评测样本详情")
    public ApiResponse<TextToSqlEvalQuestionRes> detail(@Parameter(description = "样本ID") @PathVariable Long id) {
        return ApiResponse.success(textToSqlEvalQuestionService.detail(id));
    }

    @PostMapping
    @Operation(description = "新增 Text-to-SQL 评测样本")
    public ApiResponse<IdRes> create(@Valid @RequestBody TextToSqlEvalQuestionSaveReq req, HttpServletRequest request) {
        return ApiResponse.success(textToSqlEvalQuestionService.create(req, (Long) request.getAttribute("userId")));
    }

    @PostMapping("/fromRun/{runId}")
    @Operation(description = "从运行记录加入 Text-to-SQL 评测样本")
    public ApiResponse<IdRes> createFromRun(@Parameter(description = "运行记录ID") @PathVariable Long runId, HttpServletRequest request) {
        return ApiResponse.success(textToSqlEvalQuestionService.createFromRun(runId, (Long) request.getAttribute("userId")));
    }

    @PutMapping("/{id}")
    @Operation(description = "修改 Text-to-SQL 评测样本")
    public ApiResponse<Void> update(@Parameter(description = "样本ID") @PathVariable Long id,
                                    @Valid @RequestBody TextToSqlEvalQuestionUpdateReq req) {
        textToSqlEvalQuestionService.update(id, req);
        return ApiResponse.success();
    }
}
