package cn.yanque.models.ai.texttosql.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TextToSqlEvalAssertionEntity {

    /** 判断标准ID。 */
    private Long id;
    /** 所属评测样本ID。 */
    private Long evalQuestionId;
    /** 实际取值 key，例如 business_id、used_tables、final_answer。 */
    private String actualKey;
    /** 判断方式：EQ/CONTAINS/NOT_EMPTY/EXISTS/REGEX/SEMANTIC。 */
    private String operator;
    /** 客观判断的期望值。 */
    private String expectedValue;
    /** 是否必过。 */
    private Boolean required;
    /** 权重，后续评分制使用。 */
    private BigDecimal weight;
    /** 失败归因，例如 ROUTING_ERROR、TABLE_SELECTION_ERROR。 */
    private String failureType;
    /** 主观判断参考答案。 */
    private String referenceAnswer;
    /** 主观判断必须覆盖的要点。 */
    private String keyPoints;
    /** 主观判断禁止出现的内容。 */
    private String forbiddenPoints;
    /** 主观判断最低通过分。 */
    private Integer minScore;
    /** 展示顺序。 */
    private Integer sortOrder;
    /** 规则说明。 */
    private String remark;
    /** 创建时间。 */
    private Date createdAt;
    /** 更新时间。 */
    private Date updatedAt;
}

