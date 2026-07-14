package cn.yanque.models.ai.knowledge.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class KnowledgeDocumentEntity {

    private Long id;

    private Long knowledgeBaseId;

    private String documentName;

    private String objectKey;

    private Long fileSize;

    private String version;

    private String indexStatus;

    private Integer chunkCount;

    private Integer vectorDim;

    private String errorMessage;

    private Date indexedAt;

    private Date createdAt;

    private Date updatedAt;
}
