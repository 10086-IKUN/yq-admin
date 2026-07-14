package cn.yanque.models.ai.knowledge.biz.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.knowledge.biz.KnowledgeSearchBiz;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeSearchHitDto;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeSearchReq;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeSearchRes;
import cn.yanque.models.ai.knowledge.pojo.entity.KnowledgeDocumentEntity;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeSearchReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeSearchHitRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeSearchRes;
import cn.yanque.models.ai.knowledge.service.KnowledgeBaseService;
import cn.yanque.models.ai.knowledge.service.KnowledgeDocumentService;
import cn.yanque.models.ai.knowledge.service.PythonKnowledgeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeSearchBizImpl implements KnowledgeSearchBiz {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

    @Autowired
    private PythonKnowledgeClient pythonKnowledgeClient;

    @Override
    public KnowledgeSearchRes search(KnowledgeSearchReq req) {
        knowledgeBaseService.detail(req.getKnowledgeBaseId());
        if (req.getDocumentId() != null) {
            KnowledgeDocumentEntity document = knowledgeDocumentService.getExisting(req.getDocumentId());
            if (!req.getKnowledgeBaseId().equals(document.getKnowledgeBaseId())) {
                throw BusinessException.DateError.newInstance("文档不属于当前知识库");
            }
        }

        PythonKnowledgeSearchReq pythonReq = new PythonKnowledgeSearchReq();
        pythonReq.setKnowledgeBaseId(String.valueOf(req.getKnowledgeBaseId()));
        pythonReq.setDocumentId(req.getDocumentId() == null ? null : String.valueOf(req.getDocumentId()));
        pythonReq.setQuestion(req.getQuestion().trim());
        pythonReq.setLimit(req.getLimit());

        PythonKnowledgeSearchRes pythonRes = pythonKnowledgeClient.search(pythonReq);
        KnowledgeSearchRes res = new KnowledgeSearchRes();
        res.setKnowledgeBaseId(req.getKnowledgeBaseId());
        res.setHits((pythonRes.getHits() == null ? List.<PythonKnowledgeSearchHitDto>of() : pythonRes.getHits()).stream()
                .map(this::buildHitRes)
                .toList());
        return res;
    }

    private KnowledgeSearchHitRes buildHitRes(PythonKnowledgeSearchHitDto dto) {
        KnowledgeSearchHitRes res = new KnowledgeSearchHitRes();
        res.setDocumentId(parseLong(dto.getDocumentId()));
        res.setDocumentName(dto.getDocumentName());
        res.setChunkIndex(dto.getChunkIndex());
        res.setContent(dto.getContent());
        res.setScore(dto.getScore());
        res.setMetadata(dto.getMetadata());
        return res;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.valueOf(value);
    }
}
