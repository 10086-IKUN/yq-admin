package cn.yanque.models.ai.texttosql.pojo.vo.req;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TextToSqlEvalAssertionReq {

    /** 实际取值 key，例如 business_id、used_tables、final_answer。 */
    private String actualKey;
    /** 判断方式：EQ/CONTAINS/NOT_EMPTY/EXISTS/REGEX/SEMANTIC。 */
    private String operator;
    /** 客观判断期望值。 */
    private String expectedValue;
    /** 是否必过。 */
    private Boolean required;
    /** 权重，预留给评分制。 */
    private BigDecimal weight;
    /** 失败归因。 */
    private String failureType;
    /** 主观判断参考答案。 */
    private String referenceAnswer;
    /** 主观判断必须覆盖的要点。 */
    private String keyPoints;
    /** 主观判断禁止出现的内容。 */
    private String forbiddenPoints;
    /** 主观判断最低通过分。 */
    private Integer minScore;
    /** 规则说明。 */
    private String remark;
}

