package cn.yanque.models.ai.knowledge.biz;

import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeSearchReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeSearchRes;

public interface KnowledgeSearchBiz {

    KnowledgeSearchRes search(KnowledgeSearchReq req);
}
