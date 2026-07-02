package cn.yanque.models.studentFront.ai.pojo.vo.req;

import lombok.Data;

@Data

/**
 * AI 问答新建会话请求。
 *
 * <p>标题可选；为空时服务层会使用默认标题或根据首个问题生成标题。</p>
 */
public class AiChatCreateSessionReq {

    /** 会话标题，前端不传时后端会兜底。 */
    private String title;
}
