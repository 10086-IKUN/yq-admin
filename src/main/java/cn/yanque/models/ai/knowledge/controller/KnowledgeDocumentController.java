package cn.yanque.models.ai.knowledge.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.ai.knowledge.biz.KnowledgeDocumentBiz;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeDocumentIndexReq;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeDocumentPageReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentChunksRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentDownloadRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentIndexRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentRes;
import cn.yanque.models.ai.knowledge.service.KnowledgeDocumentService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "KnowledgeDocumentController", description = "知识库文档管理")
public class KnowledgeDocumentController {

    @Autowired
    private KnowledgeDocumentBiz knowledgeDocumentBiz;

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

    @GetMapping("/knowledgeBases/{knowledgeBaseId}/documents")
    @Operation(description = "分页查询知识库文档")
    public ApiResponse<PageResult<KnowledgeDocumentRes>> page(@Parameter(description = "知识库ID") @PathVariable Long knowledgeBaseId,
                                                              @Valid @ModelAttribute KnowledgeDocumentPageReq req) {
        req.setKnowledgeBaseId(knowledgeBaseId);
        return ApiResponse.success(knowledgeDocumentService.page(req));
    }

    @PostMapping("/knowledgeBases/{knowledgeBaseId}/documents/index")
    @Operation(description = "上传知识库文档并入库")
    public ApiResponse<KnowledgeDocumentIndexRes> index(@Parameter(description = "知识库ID") @PathVariable Long knowledgeBaseId,
                                                        @Valid @RequestBody KnowledgeDocumentIndexReq req) {
        return ApiResponse.success(knowledgeDocumentBiz.index(knowledgeBaseId, req));
    }

    @PostMapping("/knowledgeDocuments/{id}/reindex")
    @Operation(description = "重新入库知识库文档")
    public ApiResponse<KnowledgeDocumentIndexRes> reindex(@Parameter(description = "文档ID") @PathVariable Long id) {
        return ApiResponse.success(knowledgeDocumentBiz.reindex(id));
    }

    @GetMapping("/knowledgeDocuments/{id}/download-url")
    @Operation(description = "获取知识库文档下载预签名")
    public ApiResponse<KnowledgeDocumentDownloadRes> downloadUrl(@Parameter(description = "文档ID") @PathVariable Long id) {
        return ApiResponse.success(knowledgeDocumentService.downloadUrl(id));
    }

    @GetMapping("/knowledgeDocuments/{id}/chunks")
    @Operation(description = "查询知识库文档切块明细")
    public ApiResponse<KnowledgeDocumentChunksRes> chunks(@Parameter(description = "文档ID") @PathVariable Long id,
                                                          @Parameter(description = "页码") Integer pageNum,
                                                          @Parameter(description = "每页条数") Integer pageSize) {
        return ApiResponse.success(knowledgeDocumentBiz.chunks(id, pageNum, pageSize));
    }

    @DeleteMapping("/knowledgeDocuments/{id}")
    @Operation(description = "删除知识库文档记录")
    public ApiResponse<IdRes> delete(@Parameter(description = "文档ID") @PathVariable Long id) {
        return ApiResponse.success(knowledgeDocumentService.delete(id));
    }
}
