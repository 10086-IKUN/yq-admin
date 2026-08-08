package cn.yanque.models.ai.prompt.pojo;

import lombok.Data;

@Data
public class PromptVersionRes {
    private Long id;
    private Long templateId;
    private Integer versionNo;
    private String content;
    private String remark;
    private String status;
    private String createdAt;
    private String updatedAt;
}
