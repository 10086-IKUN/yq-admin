package cn.yanque.models.ai.knowledge.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeBaseCreateReq;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeBasePageReq;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeBaseUpdateReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeBaseRes;

public interface KnowledgeBaseService {

    IdRes create(KnowledgeBaseCreateReq req);

    IdRes update(KnowledgeBaseUpdateReq req);

    IdRes delete(Long id);

    KnowledgeBaseRes detail(Long id);

    PageResult<KnowledgeBaseRes> page(KnowledgeBasePageReq req);
}
