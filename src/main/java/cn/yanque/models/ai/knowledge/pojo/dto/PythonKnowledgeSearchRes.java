package cn.yanque.models.ai.knowledge.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class PythonKnowledgeSearchRes {

    private String knowledgeBaseId;

    private List<PythonKnowledgeSearchHitDto> hits;
}
