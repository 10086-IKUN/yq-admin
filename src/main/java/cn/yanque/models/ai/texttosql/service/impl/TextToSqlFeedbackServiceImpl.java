package cn.yanque.models.ai.texttosql.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.texttosql.mapper.TextToSqlFeedbackMapper;
import cn.yanque.models.ai.texttosql.mapper.TextToSqlRunMapper;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlFeedbackEntity;
import cn.yanque.models.ai.texttosql.pojo.entity.TextToSqlRunEntity;
import cn.yanque.models.ai.texttosql.pojo.vo.req.TextToSqlFeedbackReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.TextToSqlFeedbackRes;
import cn.yanque.models.ai.texttosql.service.TextToSqlFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class TextToSqlFeedbackServiceImpl implements TextToSqlFeedbackService {

    private static final String RESULT_CORRECT = "CORRECT";
    private static final String RESULT_INCORRECT = "INCORRECT";
    private static final Set<String> SUPPORT_RESULTS = Set.of(RESULT_CORRECT, RESULT_INCORRECT);

    @Autowired
    private TextToSqlRunMapper textToSqlRunMapper;

    @Autowired
    private TextToSqlFeedbackMapper textToSqlFeedbackMapper;

    @Override
    public TextToSqlFeedbackRes create(TextToSqlFeedbackReq req, Long createdBy) {
        String conversationId = normalize(req.getConversationId());
        String feedbackResult = normalize(req.getFeedbackResult());
        String errorType = normalize(req.getErrorType());
        String comment = normalize(req.getComment());
        // 反馈只有“正确/有问题”两类；错误类型只在有问题时强制填写，便于后续沉淀回归样本。
        if (!SUPPORT_RESULTS.contains(feedbackResult)) {
            throw BusinessException.DateError.newInstance("反馈结果不支持");
        }
        if (RESULT_INCORRECT.equals(feedbackResult) && errorType == null) {
            throw BusinessException.DateError.newInstance("错误反馈需要选择错误类型");
        }

        TextToSqlRunEntity run = textToSqlRunMapper.selectByConversationId(conversationId);
        if (run == null || run.getId() == null) {
            throw BusinessException.DateError.newInstance("运行记录不存在");
        }

        // 反馈明细单独落表，同时把最新反馈冗余回运行记录，方便运行记录列表直接展示和筛选。
        Date now = new Date();
        TextToSqlFeedbackEntity entity = new TextToSqlFeedbackEntity();
        entity.setRunId(run.getId());
        entity.setConversationId(conversationId);
        entity.setFeedbackResult(feedbackResult);
        entity.setErrorType(errorType);
        entity.setComment(comment);
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(now);
        textToSqlFeedbackMapper.insert(entity);

        textToSqlRunMapper.updateFeedbackById(run.getId(), feedbackResult, errorType, comment, now);
        return new TextToSqlFeedbackRes(entity.getId());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

