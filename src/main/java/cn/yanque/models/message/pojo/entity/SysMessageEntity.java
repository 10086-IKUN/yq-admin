package cn.yanque.models.message.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysMessageEntity {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String msgType;
    private Long relatedId;
    private Integer isRead;
    private LocalDateTime createTime;
}
