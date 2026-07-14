package cn.yanque.models.ai.knowledge.pojo.vo.res;

import lombok.Data;

import java.util.Map;

@Data
public class KnowledgeSearchHitRes {

    private Long documentId;

    private String documentName;

    private Integer chunkIndex;

    private String content;

    private Double score;

    private Map<String, Object> metadata;
}
