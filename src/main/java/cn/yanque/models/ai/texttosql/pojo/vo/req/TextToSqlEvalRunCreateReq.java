package cn.yanque.models.ai.texttosql.pojo.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class TextToSqlEvalRunCreateReq {

    /** 评测任务名称。 */
    @NotBlank(message = "评测任务名称不能为空")
    private String name;

    /** 本次要运行的正式样本ID列表，后端只会运行 ACTIVE 样本。 */
    private List<Long> evalQuestionIds;
}

