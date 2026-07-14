package cn.yanque.models.ai.knowledge.biz;

import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeDocumentIndexReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentChunksRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentIndexRes;

public interface KnowledgeDocumentBiz {

    KnowledgeDocumentIndexRes index(Long knowledgeBaseId, KnowledgeDocumentIndexReq req);

    KnowledgeDocumentIndexRes reindex(Long documentId);

    KnowledgeDocumentChunksRes chunks(Long documentId, Integer pageNum, Integer pageSize);
}
