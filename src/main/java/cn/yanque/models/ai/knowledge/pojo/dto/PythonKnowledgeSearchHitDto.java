package cn.yanque.models.ai.knowledge.pojo.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PythonKnowledgeSearchHitDto {

    private String documentId;

    private String documentName;

    private Integer chunkIndex;

    private String content;

    private Double score;

    private Map<String, Object> metadata;
}
