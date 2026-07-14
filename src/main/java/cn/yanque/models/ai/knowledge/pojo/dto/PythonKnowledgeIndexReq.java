package cn.yanque.models.ai.knowledge.pojo.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PythonKnowledgeIndexReq {

    private String knowledgeBaseId;

    private String documentId;

    private String documentName;

    private String fileUrl;

    private String version;

    private Map<String, Object> metadata;
}
