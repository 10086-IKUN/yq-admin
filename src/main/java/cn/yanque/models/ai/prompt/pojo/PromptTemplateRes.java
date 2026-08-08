package cn.yanque.models.ai.prompt.pojo;

import lombok.Data;

@Data
public class PromptTemplateRes {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long activeVersionId;
    private Integer activeVersionNo;
    private String activeContent;
    private String status;
    private String createdAt;
    private String updatedAt;
}
