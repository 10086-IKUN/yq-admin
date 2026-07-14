package cn.yanque.models.ai.knowledge.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class KnowledgeBaseEntity {

    private Long id;

    private String knowledgeBaseName;

    private String description;

    private String status;

    private Date createdAt;

    private Date updatedAt;
}
