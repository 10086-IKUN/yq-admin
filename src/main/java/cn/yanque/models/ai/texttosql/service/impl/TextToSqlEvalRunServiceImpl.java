package cn.yanque.models.ai.texttosql.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.texttosql.mapper.TextToSqlEvalRunMapper;
import cn.yanque.models.ai.texttosql.mapper.TextToSqlEvalRunResultMapper;
import cn.yanque.models.ai.texttosql.pojo.bo.TextToSqlEvalRunQueryBo;
import cn.yanque.models.ai.texttosql.pojo.bo.TextToSqlEvalRunResultQueryBo;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalRunEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlEvalRunResultEntity;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlEvalRunResultPageReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalRunRes;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlEvalRunResultRes;
import cn.yanque.models.ai.texttosql.service.TextToSqlEvalRunService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TextToSqlEvalRunServiceImpl implements TextToSqlEvalRunService {

    @Autowired
    private TextToSqlEvalRunMapper textToSqlEvalRunMapper;

    @Autowired
    private TextToSqlEvalRunResultMapper textToSqlEvalRunResultMapper;

    @Override
    public void insertRun(TextToSqlEvalRunEntity entity) {
        textToSqlEvalRunMapper.insert(entity);
    }

    @Override
    public void updateRunById(TextToSqlEvalRunEntity entity) {
        textToSqlEvalRunMapper.updateById(entity);
    }

    @Override
    public void updateRunSummaryById(TextToSqlEvalRunEntity entity, boolean clearFinishedAt) {
        textToSqlEvalRunMapper.updateSummaryById(entity, clearFinishedAt);
    }

    @Override
    public TextToSqlEvalRunResultEntity selectResultEntityById(Long id) {
        return textToSqlEvalRunResultMapper.selectById(id);
    }

    @Override
    public List<TextToSqlEvalRunResultEntity> selectResultEntitiesByEvalRunId(Long evalRunId) {
        return textToSqlEvalRunResultMapper.selectByEvalRunId(evalRunId);
    }

    @Override
    public void insertResult(TextToSqlEvalRunResultEntity entity) {
        textToSqlEvalRunResultMapper.insert(entity);
    }

    @Override
    public void updateResultById(TextToSqlEvalRunResultEntity entity) {
        textToSqlEvalRunResultMapper.updateById(entity);
    }

    @Override
    public void markResultClarificationRunning(Long id, String failureType, String failureReason) {
        textToSqlEvalRunResultMapper.markClarificationRunningById(id, failureType, failureReason);
    }

    @Override
    public void markResultClarificationRunError(Long id, String failureType, String failureReason, String executionError, Long durationMs) {
        textToSqlEvalRunResultMapper.markClarificationRunErrorById(id, failureType, failureReason, executionError, durationMs);
    }

    @Override
    public PageResult<TextToSqlEvalRunRes> page(TextToSqlEvalRunPageReq req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        TextToSqlEvalRunQueryBo query = new TextToSqlEvalRunQueryBo();
        BeanUtils.copyProperties(req, query);
        // 任务列表只按任务名称和状态过滤；结果明细过滤放在 resultPage 里处理。
        query.setKeyword(normalizeBlank(query.getKeyword()));
        query.setStatus(normalizeBlank(query.getStatus()));
        PageHelper.startPage(pageNum, pageSize);
        List<TextToSqlEvalRunEntity> list = textToSqlEvalRunMapper.selectPage(query);
        PageInfo<TextToSqlEvalRunEntity> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, list.stream().map(this::buildRunRes).toList());
    }

    @Override
    public TextToSqlEvalRunRes detail(Long id) {
        TextToSqlEvalRunEntity entity = textToSqlEvalRunMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.DateError.newInstance("评测任务不存在");
        }
        return buildRunRes(entity);
    }

    @Override
    public PageResult<TextToSqlEvalRunResultRes> resultPage(Long evalRunId, TextToSqlEvalRunResultPageReq req) {
        if (textToSqlEvalRunMapper.selectById(evalRunId) == null) {
            throw BusinessException.DateError.newInstance("评测任务不存在");
        }
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        TextToSqlEvalRunResultQueryBo query = new TextToSqlEvalRunResultQueryBo();
        BeanUtils.copyProperties(req, query);
        query.setEvalRunId(evalRunId);
        query.setKeyword(normalizeBlank(query.getKeyword()));
        query.setFailureType(normalizeBlank(query.getFailureType()));
        PageHelper.startPage(pageNum, pageSize);
        List<TextToSqlEvalRunResultEntity> list = textToSqlEvalRunResultMapper.selectPage(query);
        PageInfo<TextToSqlEvalRunResultEntity> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, list.stream().map(this::buildResultRes).toList());
    }

    private TextToSqlEvalRunRes buildRunRes(TextToSqlEvalRunEntity entity) {
        TextToSqlEvalRunRes res = new TextToSqlEvalRunRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private TextToSqlEvalRunResultRes buildResultRes(TextToSqlEvalRunResultEntity entity) {
        TextToSqlEvalRunResultRes res = new TextToSqlEvalRunResultRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private String normalizeBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

