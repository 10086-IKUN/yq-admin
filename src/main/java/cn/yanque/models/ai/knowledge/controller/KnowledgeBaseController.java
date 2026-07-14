package cn.yanque.models.ai.knowledge.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeBaseCreateReq;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeBasePageReq;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeBaseUpdateReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeBaseRes;
import cn.yanque.models.ai.knowledge.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledgeBases")
@Tag(name = "KnowledgeBaseController", description = "知识库管理")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @PostMapping
    @Operation(description = "新增知识库")
    public ApiResponse<IdRes> create(@Valid @RequestBody KnowledgeBaseCreateReq req) {
        return ApiResponse.success(knowledgeBaseService.create(req));
    }

    @PutMapping("{id}")
    @Operation(description = "修改知识库")
    public ApiResponse<IdRes> update(@Parameter(description = "知识库ID") @PathVariable Long id,
                                     @Valid @RequestBody KnowledgeBaseUpdateReq req) {
        req.setId(id);
        return ApiResponse.success(knowledgeBaseService.update(req));
    }

    @DeleteMapping("{id}")
    @Operation(description = "删除知识库")
    public ApiResponse<IdRes> delete(@Parameter(description = "知识库ID") @PathVariable Long id) {
        return ApiResponse.success(knowledgeBaseService.delete(id));
    }

    @GetMapping("{id}")
    @Operation(description = "查询知识库详情")
    public ApiResponse<KnowledgeBaseRes> detail(@Parameter(description = "知识库ID") @PathVariable Long id) {
        return ApiResponse.success(knowledgeBaseService.detail(id));
    }

    @GetMapping
    @Operation(description = "分页查询知识库")
    public ApiResponse<PageResult<KnowledgeBaseRes>> page(@Valid @ModelAttribute KnowledgeBasePageReq req) {
        return ApiResponse.success(knowledgeBaseService.page(req));
    }
}
