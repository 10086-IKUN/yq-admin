package cn.yanque.models.ai.knowledge.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识库文档分页请求")
public class KnowledgeDocumentPageReq {

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "文档名称")
    private String documentName;

    @Schema(description = "入库状态")
    private String indexStatus;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
