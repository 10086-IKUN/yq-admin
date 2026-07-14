package cn.yanque.models.ai.knowledge.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "知识库语义检索请求")
public class KnowledgeSearchReq {

    @NotNull(message = "知识库ID不能为空")
    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "文档ID，不传则检索整个知识库")
    private Long documentId;

    @NotBlank(message = "问题不能为空")
    @Schema(description = "检索问题")
    private String question;

    @Min(value = 1, message = "返回数量不能小于1")
    @Max(value = 20, message = "返回数量不能大于20")
    @Schema(description = "返回 chunk 数量")
    private Integer limit = 5;
}
