package cn.yanque.models.ai.knowledge.pojo.vo.res;

import lombok.Data;

import java.util.Map;

@Data
public class KnowledgeDocumentChunkRes {

    private Integer chunkIndex;

    private String content;

    private String headerPath;

    private String source;

    private Map<String, Object> metadata;
}
