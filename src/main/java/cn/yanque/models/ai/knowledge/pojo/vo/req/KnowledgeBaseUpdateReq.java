package cn.yanque.models.ai.knowledge.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "修改知识库请求")
public class KnowledgeBaseUpdateReq {

    @Schema(description = "知识库ID")
    private Long id;

    @NotBlank(message = "知识库名称不能为空")
    @Schema(description = "知识库名称")
    private String knowledgeBaseName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态：ACTIVE/INACTIVE")
    private String status;
}
