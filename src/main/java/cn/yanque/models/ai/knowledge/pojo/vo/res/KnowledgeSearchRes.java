package cn.yanque.models.ai.knowledge.pojo.vo.res;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgeSearchRes {

    private Long knowledgeBaseId;

    private List<KnowledgeSearchHitRes> hits;
}
