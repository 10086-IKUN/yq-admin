package cn.yanque.models.ai.knowledge.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.knowledge.config.AiKnowledgeProperties;
import cn.yanque.models.ai.knowledge.mapper.KnowledgeBaseMapper;
import cn.yanque.models.ai.knowledge.mapper.KnowledgeDocumentMapper;
import cn.yanque.models.ai.knowledge.pojo.bo.KnowledgeDocumentQueryBo;
import cn.yanque.models.ai.knowledge.pojo.dto.KnowledgeChatRagContext;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeSearchHitDto;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeSearchReq;
import cn.yanque.models.ai.knowledge.pojo.dto.PythonKnowledgeSearchRes;
import cn.yanque.models.ai.knowledge.pojo.entity.KnowledgeDocumentEntity;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeDocumentPageReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentDownloadRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeDocumentRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeSearchHitRes;
import cn.yanque.models.ai.knowledge.service.KnowledgeDocumentService;
import cn.yanque.models.ai.knowledge.service.PythonKnowledgeClient;
import cn.yanque.models.file.service.FileService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class KnowledgeDocumentServiceImpl implements KnowledgeDocumentService {

    private static final int DEFAULT_LIMIT = 5;

    private static final int MAX_LIMIT = 20;

    private static final String STATUS_PENDING = "PENDING";

    private static final String STATUS_INDEXING = "INDEXING";

    private static final String STATUS_INDEXED = "INDEXED";

    private static final String STATUS_FAILED = "FAILED";

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private AiKnowledgeProperties properties;

    @Autowired
    private PythonKnowledgeClient pythonKnowledgeClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeDocumentEntity createPending(Long knowledgeBaseId, String documentName, String objectKey, Long fileSize, String version) {
        if (knowledgeBaseMapper.selectById(knowledgeBaseId) == null) {
            throw BusinessException.DateError.newInstance("知识库不存在");
        }
        String normalizedName = normalizeDocumentName(documentName);
        String normalizedObjectKey = normalizeObjectKey(objectKey);
        KnowledgeDocumentEntity entity = new KnowledgeDocumentEntity();
        entity.setKnowledgeBaseId(knowledgeBaseId);
        entity.setDocumentName(normalizedName);
        entity.setObjectKey(normalizedObjectKey);
        entity.setFileSize(fileSize);
        entity.setVersion(normalizeBlank(version));
        entity.setIndexStatus(STATUS_PENDING);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(entity.getCreatedAt());
        knowledgeDocumentMapper.insert(entity);
        return entity;
    }

    @Override
    public KnowledgeDocumentEntity getExisting(Long id) {
        KnowledgeDocumentEntity entity = knowledgeDocumentMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.DateError.newInstance("知识库文档不存在");
        }
        return entity;
    }

    @Override
    public void markIndexing(Long id) {
        knowledgeDocumentMapper.updateIndexing(id, new Date());
    }

    @Override
    public void markIndexed(Long id, Integer chunkCount, Integer vectorDim) {
        KnowledgeDocumentEntity entity = new KnowledgeDocumentEntity();
        entity.setId(id);
        entity.setIndexStatus(STATUS_INDEXED);
        entity.setChunkCount(chunkCount);
        entity.setVectorDim(vectorDim);
        entity.setErrorMessage(null);
        entity.setIndexedAt(new Date());
        entity.setUpdatedAt(entity.getIndexedAt());
        knowledgeDocumentMapper.updateIndexed(entity);
    }

    @Override
    public void markFailed(Long id, String errorMessage) {
        KnowledgeDocumentEntity entity = new KnowledgeDocumentEntity();
        entity.setId(id);
        entity.setIndexStatus(STATUS_FAILED);
        entity.setErrorMessage(errorMessage == null ? "入库失败" : errorMessage);
        entity.setUpdatedAt(new Date());
        knowledgeDocumentMapper.updateFailed(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdRes delete(Long id) {
        if (knowledgeDocumentMapper.deleteById(id) == 0) {
            throw BusinessException.DateError.newInstance("知识库文档不存在");
        }
        return new IdRes(id);
    }

    @Override
    public KnowledgeDocumentDownloadRes downloadUrl(Long id) {
        KnowledgeDocumentEntity entity = getExisting(id);
        KnowledgeDocumentDownloadRes res = new KnowledgeDocumentDownloadRes();
        res.setDownloadUrl(fileService.download(entity.getObjectKey()));
        res.setObjectKey(entity.getObjectKey());
        return res;
    }

    @Override
    public PageResult<KnowledgeDocumentRes> page(KnowledgeDocumentPageReq req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        KnowledgeDocumentQueryBo query = new KnowledgeDocumentQueryBo();
        BeanUtils.copyProperties(req, query);
        PageHelper.startPage(pageNum, pageSize);
        List<KnowledgeDocumentEntity> list = knowledgeDocumentMapper.selectPage(query);
        PageInfo<KnowledgeDocumentEntity> pageInfo = new PageInfo<>(list);
        List<KnowledgeDocumentRes> records = list.stream().map(this::buildRes).toList();
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, records);
    }

    @Override
    public KnowledgeChatRagContext buildChatContext(String question) {
        if (!Boolean.TRUE.equals(properties.getChatRagEnabled()) || isBlank(properties.getChatKnowledgeBaseId())) {
            return KnowledgeChatRagContext.empty();
        }
        try {
            PythonKnowledgeSearchReq req = new PythonKnowledgeSearchReq();
            req.setKnowledgeBaseId(properties.getChatKnowledgeBaseId().trim());
            req.setQuestion(question);
            req.setLimit(safeLimit(properties.getChatTopK()));
            PythonKnowledgeSearchRes searchRes = pythonKnowledgeClient.search(req);
            List<KnowledgeSearchHitRes> hits = (searchRes.getHits() == null ? List.<PythonKnowledgeSearchHitDto>of() : searchRes.getHits())
                    .stream()
                    .map(this::buildHitRes)
                    .toList();
            if (hits.isEmpty()) {
                return KnowledgeChatRagContext.empty();
            }
            KnowledgeChatRagContext context = new KnowledgeChatRagContext();
            context.setUsed(true);
            context.setReferences(hits);
            context.setPrompt(buildChatPrompt(question, hits));
            return context;
        } catch (Exception e) {
            log.warn("knowledge RAG skipped, reason={}", e.getMessage());
            return KnowledgeChatRagContext.empty();
        }
    }

    private KnowledgeDocumentRes buildRes(KnowledgeDocumentEntity entity) {
        KnowledgeDocumentRes res = new KnowledgeDocumentRes();
        BeanUtils.copyProperties(entity, res);
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

    private String buildChatPrompt(String question, List<KnowledgeSearchHitRes> hits) {
        StringBuilder builder = new StringBuilder();
        builder.append("Use the following knowledge snippets first. ");
        builder.append("If the snippets do not contain the answer, say the knowledge base has no clear answer and avoid guessing.\n\n");
        int totalChars = 0;
        int index = 1;
        for (KnowledgeSearchHitRes hit : hits) {
            String content = abbreviate(hit.getContent(), safePositive(properties.getChatMaxCharsPerChunk(), 900));
            if (totalChars + content.length() > safePositive(properties.getChatMaxTotalChars(), 3000)) {
                break;
            }
            totalChars += content.length();
            builder.append("[Reference ").append(index++).append("] document=")
                    .append(nullToEmpty(hit.getDocumentName()))
                    .append(", chunk=").append(hit.getChunkIndex())
                    .append(", score=").append(hit.getScore())
                    .append("\n")
                    .append(content)
                    .append("\n\n");
        }
        builder.append("Student question:\n").append(question);
        return builder.toString();
    }

    private String normalizeDocumentName(String value) {
        if (value == null || value.isBlank()) {
            throw BusinessException.DateError.newInstance("文档名称不能为空");
        }
        String normalized = value.trim();
        if (!normalized.toLowerCase().endsWith(".md")) {
            throw BusinessException.DateError.newInstance("当前知识库文档只支持md格式");
        }
        return normalized;
    }

    private String normalizeObjectKey(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw BusinessException.DateError.newInstance("对象Key不能为空");
        }
        String normalized = objectKey.trim();
        if (normalized.contains("..") || normalized.startsWith("/") || normalized.startsWith("http://") || normalized.startsWith("https://")) {
            throw BusinessException.DateError.newInstance("知识库文档对象Key不允许访问");
        }
        if (!normalized.toLowerCase().endsWith(".md")) {
            throw BusinessException.DateError.newInstance("当前知识库文档只支持md格式");
        }
        return normalized;
    }

    private String normalizeBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private int safeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private int safePositive(Integer value, int defaultValue) {
        return value == null || value <= 0 ? defaultValue : value;
    }

    private String abbreviate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
