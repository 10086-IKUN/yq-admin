package cn.yanque.models.ai.knowledge.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.enums.ActiveEnum;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.knowledge.mapper.KnowledgeBaseMapper;
import cn.yanque.models.ai.knowledge.mapper.KnowledgeDocumentMapper;
import cn.yanque.models.ai.knowledge.pojo.bo.KnowledgeBaseQueryBo;
import cn.yanque.models.ai.knowledge.pojo.entity.KnowledgeBaseEntity;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeBaseCreateReq;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeBasePageReq;
import cn.yanque.models.ai.knowledge.pojo.vo.req.KnowledgeBaseUpdateReq;
import cn.yanque.models.ai.knowledge.pojo.vo.res.IdRes;
import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeBaseRes;
import cn.yanque.models.ai.knowledge.service.KnowledgeBaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdRes create(KnowledgeBaseCreateReq req) {
        KnowledgeBaseEntity entity = new KnowledgeBaseEntity();
        entity.setKnowledgeBaseName(normalizeName(req.getKnowledgeBaseName()));
        entity.setDescription(normalizeBlank(req.getDescription()));
        entity.setStatus(normalizeStatus(req.getStatus()));
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(entity.getCreatedAt());
        try {
            knowledgeBaseMapper.insert(entity);
        } catch (DuplicateKeyException e) {
            throw BusinessException.DateExist.newInstance("知识库名称已存在");
        }
        return new IdRes(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdRes update(KnowledgeBaseUpdateReq req) {
        if (knowledgeBaseMapper.selectById(req.getId()) == null) {
            throw BusinessException.DateError.newInstance("知识库不存在");
        }
        KnowledgeBaseEntity entity = new KnowledgeBaseEntity();
        entity.setId(req.getId());
        entity.setKnowledgeBaseName(normalizeName(req.getKnowledgeBaseName()));
        entity.setDescription(normalizeBlank(req.getDescription()));
        entity.setStatus(normalizeStatus(req.getStatus()));
        entity.setUpdatedAt(new Date());
        try {
            if (knowledgeBaseMapper.updateById(entity) == 0) {
                throw BusinessException.DateError.newInstance("知识库不存在");
            }
        } catch (DuplicateKeyException e) {
            throw BusinessException.DateExist.newInstance("知识库名称已存在");
        }
        return new IdRes(req.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdRes delete(Long id) {
        if (knowledgeDocumentMapper.countByKnowledgeBaseId(id) > 0) {
            throw BusinessException.DateError.newInstance("知识库下还有文档，不能删除");
        }
        if (knowledgeBaseMapper.deleteById(id) == 0) {
            throw BusinessException.DateError.newInstance("知识库不存在");
        }
        return new IdRes(id);
    }

    @Override
    public KnowledgeBaseRes detail(Long id) {
        KnowledgeBaseEntity entity = knowledgeBaseMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.DateError.newInstance("知识库不存在");
        }
        return buildRes(entity);
    }

    @Override
    public PageResult<KnowledgeBaseRes> page(KnowledgeBasePageReq req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        KnowledgeBaseQueryBo query = new KnowledgeBaseQueryBo();
        BeanUtils.copyProperties(req, query);
        PageHelper.startPage(pageNum, pageSize);
        List<KnowledgeBaseEntity> list = knowledgeBaseMapper.selectPage(query);
        PageInfo<KnowledgeBaseEntity> pageInfo = new PageInfo<>(list);
        List<KnowledgeBaseRes> records = list.stream().map(this::buildRes).toList();
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, records);
    }

    private KnowledgeBaseRes buildRes(KnowledgeBaseEntity entity) {
        KnowledgeBaseRes res = new KnowledgeBaseRes();
        BeanUtils.copyProperties(entity, res);
        res.setDocumentCount(knowledgeDocumentMapper.countByKnowledgeBaseId(entity.getId()));
        return res;
    }

    private String normalizeName(String value) {
        if (value == null || value.isBlank()) {
            throw BusinessException.DateError.newInstance("知识库名称不能为空");
        }
        return value.trim();
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return ActiveEnum.ACTIVE.name();
        }
        String normalized = status.trim();
        if (!ActiveEnum.ACTIVE.name().equals(normalized) && !ActiveEnum.INACTIVE.name().equals(normalized)) {
            throw BusinessException.DateError.newInstance("状态只能是ACTIVE或INACTIVE");
        }
        return normalized;
    }

    private String normalizeBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
