package cn.yanque.models.ai.knowledge.pojo.vo.res;

import lombok.Data;

@Data
public class KnowledgeDocumentIndexRes {

    private Long id;

    private String indexStatus;

    private Integer chunkCount;

    private Integer vectorDim;
}
