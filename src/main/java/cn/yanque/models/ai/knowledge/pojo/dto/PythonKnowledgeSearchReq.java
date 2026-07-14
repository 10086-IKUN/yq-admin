package cn.yanque.models.ai.knowledge.pojo.dto;

import lombok.Data;

@Data
public class PythonKnowledgeSearchReq {

    private String knowledgeBaseId;

    private String documentId;

    private String question;

    private Integer limit;
}
