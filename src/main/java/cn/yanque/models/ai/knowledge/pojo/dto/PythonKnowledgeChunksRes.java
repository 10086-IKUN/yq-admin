package cn.yanque.models.ai.knowledge.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class PythonKnowledgeChunksRes {

    private String knowledgeBaseId;

    private String documentId;

    private Long total;

    private Integer pageNum;

    private Integer pageSize;

    private List<PythonKnowledgeChunkDto> chunks;
}
