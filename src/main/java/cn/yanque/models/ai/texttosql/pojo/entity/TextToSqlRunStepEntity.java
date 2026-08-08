package cn.yanque.models.ai.texttosql.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TextToSqlRunStepEntity {

    /** 运行步骤ID。 */
    private Long id;
    /** 所属运行记录ID。 */
    private Long runId;
    /** LangGraph 会话ID。 */
    private String conversationId;
    /** 步骤序号，按 graph 执行顺序递增。 */
    private Integer stepIndex;
    /** 当前节点名称，例如 classify_question、generate_sql。 */
    private String nodeName;
    /** 状态来源，通常来自 LangGraph checkpoint 或流式事件。 */
    private String source;
    /** LangGraph 原始 step 编号。 */
    private Integer langGraphStep;
    /** 下一批节点，JSON 字符串。 */
    private String nextNodes;
    /** 当前步骤后的 State 快照 JSON，用于排查路由、选表、澄清等中间判断。 */
    private String stateSnapshotJson;
    /** LangGraph checkpoint 创建时间，保留原始字符串。 */
    private String checkpointCreatedAt;
    /** 保存到业务库的时间。 */
    private Date savedAt;
}

