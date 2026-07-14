package cn.yanque.models.ai.knowledge.pojo.vo.res;

import lombok.Data;

@Data
public class KnowledgeDocumentDownloadRes {

    private String downloadUrl;

    private String objectKey;

    private Long expires;
}
