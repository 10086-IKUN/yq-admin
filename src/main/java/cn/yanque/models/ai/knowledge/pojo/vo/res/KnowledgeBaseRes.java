package cn.yanque.models.ai.knowledge.pojo.vo.res;

import lombok.Data;

import java.util.Date;

@Data
public class KnowledgeBaseRes {

    private Long id;

    private String knowledgeBaseName;

    private String description;

    private String status;

    private Integer documentCount;

    private Date createdAt;

    private Date updatedAt;
}
