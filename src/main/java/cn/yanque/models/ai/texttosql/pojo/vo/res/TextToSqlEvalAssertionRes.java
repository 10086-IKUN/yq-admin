package cn.yanque.models.ai.texttosql.pojo.vo.res;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TextToSqlEvalAssertionRes {

    /** 判断标准ID。 */
    private Long id;
    /** 所属评测样本ID。 */
    private Long evalQuestionId;
    /** 实际取值 key。 */
    private String actualKey;
    /** 判断方式。 */
    private String operator;
    /** 期望值。 */
    private String expectedValue;
    /** 是否必过。 */
    private Boolean required;
    /** 权重。 */
    private BigDecimal weight;
    /** 失败归因。 */
    private String failureType;
    /** 主观参考答案。 */
    private String referenceAnswer;
    /** 主观必须覆盖要点。 */
    private String keyPoints;
    /** 主观禁止内容。 */
    private String forbiddenPoints;
    /** 主观最低分。 */
    private Integer minScore;
    /** 展示顺序。 */
    private Integer sortOrder;
    /** 说明。 */
    private String remark;
}

