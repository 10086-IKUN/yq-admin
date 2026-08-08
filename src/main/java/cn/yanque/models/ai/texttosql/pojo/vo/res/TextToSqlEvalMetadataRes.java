package cn.yanque.models.ai.texttosql.pojo.vo.res;

import lombok.Data;

import java.util.List;

@Data
public class TextToSqlEvalMetadataRes {

    /** 评测目标选项，例如端到端效果、SQL生成、澄清判断。 */
    private List<TextToSqlEvalOptionRes> evalTargets;
    /** 样本场景选项，例如正常样本、边界样本、回归问题。 */
    private List<TextToSqlEvalOptionRes> sampleCategories;
    /** 判断方式选项，value 必须是评测引擎已支持的 operator。 */
    private List<TextToSqlEvalOptionRes> assertionOperators;
    /** 失败归因选项，用于评测报告里的失败原因分布。 */
    private List<TextToSqlEvalOptionRes> failureTypes;
}

