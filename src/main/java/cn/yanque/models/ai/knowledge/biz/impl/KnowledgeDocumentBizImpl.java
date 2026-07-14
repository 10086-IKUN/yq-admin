package cn.yanque.models.ai.knowledge.biz.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.knowledge.biz.KnowledgeDocumentBiz;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeChunkDto;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeChunksReq;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeChunksRes;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeIndexReq;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeIndexRes;
import cn.yanque.models.ai.knowledge.pojo.entity.KnowledgeDocumentEntity;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeDocumentIndexReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentChunkRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentChunksRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentIndexRes;
import cn.yanque.models.ai.knowledge.service.KnowledgeDocumentService;
import cn.yanque.models.ai.knowledge.service.PythonKnowledgeClient;
import cn.yanque.models.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class KnowledgeDocumentBizImpl implements KnowledgeDocumentBiz {

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PythonKnowledgeClient pythonKnowledgeClient;

    @Override
    public KnowledgeDocumentIndexRes index(Long knowledgeBaseId, KnowledgeDocumentIndexReq req) {
        KnowledgeDocumentEntity document = knowledgeDocumentService.createPending(
                knowledgeBaseId,
                req.getDocumentName(),
                req.getObjectKey(),
                req.getFileSize(),
                req.getVersion());
        return doIndex(document);
    }

    @Override
    public KnowledgeDocumentIndexRes reindex(Long documentId) {
        return doIndex(knowledgeDocumentService.getExisting(documentId));
    }

    @Override
    public KnowledgeDocumentChunksRes chunks(Long documentId, Integer pageNum, Integer pageSize) {
        KnowledgeDocumentEntity document = knowledgeDocumentService.getExisting(documentId);
        PythonKnowledgeChunksRes pythonRes = pythonKnowledgeClient.listDocumentChunks(buildPythonChunksReq(document, pageNum, pageSize));

        KnowledgeDocumentChunksRes res = new KnowledgeDocumentChunksRes();
        res.setKnowledgeBaseId(document.getKnowledgeBaseId());
        res.setDocumentId(document.getId());
        res.setDocumentName(document.getDocumentName());
        // chunk 总数以 Java 文档表入库结果为准，Python 只按页查询当前页 chunk 明细。
        res.setTotal(document.getChunkCount() == null ? 0L : document.getChunkCount().longValue());
        res.setPageNum(pythonRes.getPageNum());
        res.setPageSize(pythonRes.getPageSize());
        res.setChunks(buildChunkResList(pythonRes.getChunks()));
        return res;
    }

    private KnowledgeDocumentIndexRes doIndex(KnowledgeDocumentEntity document) {
        knowledgeDocumentService.markIndexing(document.getId());
        try {
            // Java 只把短期下载地址交给 Python。Python 不直接拿 OSS AK/SK，也不依赖 Java 本地文件路径。
            PythonKnowledgeIndexRes pythonRes = pythonKnowledgeClient.indexDocument(buildPythonReq(document, fileService.download(document.getObjectKey())));
            knowledgeDocumentService.markIndexed(document.getId(), pythonRes.getChunkCount(), pythonRes.getVectorDim());
            return buildIndexRes(document.getId(), "INDEXED", pythonRes.getChunkCount(), pythonRes.getVectorDim());
        } catch (Exception e) {
            knowledgeDocumentService.markFailed(document.getId(), e.getMessage());
            if (e instanceof BusinessException businessException) {
                throw businessException;
            }
            throw BusinessException.RemoteError.newInstance("知识库文档入库失败");
        }
    }

    private PythonKnowledgeIndexReq buildPythonReq(KnowledgeDocumentEntity document, String fileUrl) {
        PythonKnowledgeIndexReq req = new PythonKnowledgeIndexReq();
        req.setKnowledgeBaseId(String.valueOf(document.getKnowledgeBaseId()));
        req.setDocumentId(String.valueOf(document.getId()));
        req.setDocumentName(document.getDocumentName());
        req.setFileUrl(fileUrl);
        req.setVersion(document.getVersion());

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("objectKey", document.getObjectKey());
        metadata.put("fileSize", document.getFileSize());
        req.setMetadata(metadata);
        return req;
    }

    private PythonKnowledgeChunksReq buildPythonChunksReq(KnowledgeDocumentEntity document, Integer pageNum, Integer pageSize) {
        PythonKnowledgeChunksReq req = new PythonKnowledgeChunksReq();
        req.setKnowledgeBaseId(String.valueOf(document.getKnowledgeBaseId()));
        req.setDocumentId(String.valueOf(document.getId()));
        req.setPageNum(pageNum == null || pageNum <= 0 ? 1 : pageNum);
        req.setPageSize(pageSize == null || pageSize <= 0 ? 10 : Math.min(pageSize, 100));
        return req;
    }

    private List<KnowledgeDocumentChunkRes> buildChunkResList(List<PythonKnowledgeChunkDto> chunks) {
        if (chunks == null) {
            return List.of();
        }
        return chunks.stream().map(this::buildChunkRes).toList();
    }

    private KnowledgeDocumentChunkRes buildChunkRes(PythonKnowledgeChunkDto dto) {
        KnowledgeDocumentChunkRes res = new KnowledgeDocumentChunkRes();
        res.setChunkIndex(dto.getChunkIndex());
        res.setContent(dto.getContent());
        res.setHeaderPath(dto.getHeaderPath());
        res.setSource(dto.getSource());
        res.setMetadata(dto.getMetadata());
        return res;
    }

    private KnowledgeDocumentIndexRes buildIndexRes(Long id, String status, Integer chunkCount, Integer vectorDim) {
        KnowledgeDocumentIndexRes res = new KnowledgeDocumentIndexRes();
        res.setId(id);
        res.setIndexStatus(status);
        res.setChunkCount(chunkCount);
        res.setVectorDim(vectorDim);
        return res;
    }
}
