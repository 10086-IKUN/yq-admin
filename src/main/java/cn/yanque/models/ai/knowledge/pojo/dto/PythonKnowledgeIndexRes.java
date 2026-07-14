package cn.yanque.models.ai.knowledge.pojo.dto;

import lombok.Data;

@Data
public class PythonKnowledgeIndexRes {

    private String knowledgeBaseId;

    private String documentId;

    private String documentName;

    private String collectionName;

    private Integer chunkCount;

    private Integer vectorDim;
}
