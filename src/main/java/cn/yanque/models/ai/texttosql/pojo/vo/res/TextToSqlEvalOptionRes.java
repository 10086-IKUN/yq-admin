package cn.yanque.models.ai.texttosql.pojo.vo.res;

import lombok.Data;

@Data
public class TextToSqlEvalOptionRes {

    /** 页面展示名称。 */
    private String label;
    /** 业务保存值。 */
    private String value;
    /** Ant Design Tag 颜色，可为空。 */
    private String color;
}

