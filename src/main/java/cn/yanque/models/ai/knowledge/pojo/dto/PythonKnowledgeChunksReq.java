package cn.yanque.models.ai.knowledge.pojo.dto;

import lombok.Data;

@Data
public class PythonKnowledgeChunksReq {

    private String knowledgeBaseId;

    private String documentId;

    private Integer pageNum;

    private Integer pageSize;
}
