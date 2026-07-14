package cn.yanque.models.ai.knowledge.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "知识库文档入库请求")
public class KnowledgeDocumentIndexReq {

    @NotBlank(message = "文档名称不能为空")
    @Schema(description = "文档名称")
    private String documentName;

    @NotBlank(message = "对象Key不能为空")
    @Schema(description = "TOS对象Key")
    private String objectKey;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "版本号")
    private String version;
}
