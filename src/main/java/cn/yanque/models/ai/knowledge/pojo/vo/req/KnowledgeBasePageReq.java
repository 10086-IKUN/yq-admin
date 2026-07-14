package cn.yanque.models.ai.knowledge.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识库分页请求")
public class KnowledgeBasePageReq {

    @Schema(description = "知识库名称")
    private String knowledgeBaseName;

    @Schema(description = "状态")
    private String status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
