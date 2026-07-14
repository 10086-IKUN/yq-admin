package cn.yanque.models.ai.knowledge.pojo.dto;

import cn.yanque.models.ai.knowledge.pojo.vo.res.KnowledgeSearchHitRes;
import lombok.Data;

import java.util.List;

@Data
public class KnowledgeChatRagContext {

    private boolean used;

    private String prompt;

    private List<KnowledgeSearchHitRes> references;

    public static KnowledgeChatRagContext empty() {
        KnowledgeChatRagContext context = new KnowledgeChatRagContext();
        context.setUsed(false);
        context.setReferences(List.of());
        return context;
    }
}
