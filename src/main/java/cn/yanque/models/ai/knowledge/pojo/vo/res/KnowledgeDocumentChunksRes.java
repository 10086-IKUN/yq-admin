package cn.yanque.models.ai.knowledge.pojo.vo.res;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgeDocumentChunksRes {

    private Long knowledgeBaseId;

    private Long documentId;

    private String documentName;

    private Long total;

    private Integer pageNum;

    private Integer pageSize;

    private List<KnowledgeDocumentChunkRes> chunks;
}
