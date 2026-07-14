package cn.yanque.models.ai.knowledge.pojo.bo;

import lombok.Data;

@Data
public class KnowledgeDocumentQueryBo {

    private Long knowledgeBaseId;

    private String documentName;

    private String indexStatus;
}
