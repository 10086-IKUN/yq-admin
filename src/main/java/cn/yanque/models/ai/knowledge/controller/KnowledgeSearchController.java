package cn.yanque.models.ai.knowledge.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.ai.knowledge.biz.KnowledgeSearchBiz;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeSearchReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeSearchRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledgeSearch")
@Tag(name = "KnowledgeSearchController", description = "知识库检索")
public class KnowledgeSearchController {

    @Autowired
    private KnowledgeSearchBiz knowledgeSearchBiz;

    @PostMapping("/search")
    @Operation(description = "知识库语义检索")
    public ApiResponse<KnowledgeSearchRes> search(@Valid @RequestBody KnowledgeSearchReq req) {
        return ApiResponse.success(knowledgeSearchBiz.search(req));
    }
}
