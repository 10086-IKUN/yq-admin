package cn.yanque.models.message.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data

/**
 * SysMessageEntity 数据库实体对象。
 *
 * <p>字段与对应业务表保持映射关系，供 MyBatis 查询和写入使用。</p>
 */
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
