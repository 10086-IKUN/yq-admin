package cn.yanque.models.ai.knowledge.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.ai.knowledge.pojo.dto.KnowledgeChatRagContext;
import cn.yanque.models.ai.knowledge.pojo.entity.KnowledgeDocumentEntity;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeDocumentPageReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentDownloadRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentRes;

public interface KnowledgeDocumentService {

    KnowledgeDocumentEntity createPending(Long knowledgeBaseId, String documentName, String objectKey, Long fileSize, String version);

    KnowledgeDocumentEntity getExisting(Long id);

    void markIndexing(Long id);

    void markIndexed(Long id, Integer chunkCount, Integer vectorDim);

    void markFailed(Long id, String errorMessage);

    IdRes delete(Long id);

    KnowledgeDocumentDownloadRes downloadUrl(Long id);

    PageResult<KnowledgeDocumentRes> page(KnowledgeDocumentPageReq req);

    KnowledgeChatRagContext buildChatContext(String question);
}
