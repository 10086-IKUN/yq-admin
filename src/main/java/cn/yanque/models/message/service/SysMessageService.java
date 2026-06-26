package cn.yanque.models.message.service;

import cn.yanque.models.message.pojo.entity.SysMessageEntity;

import java.util.List;

public interface SysMessageService {

    /**
     * 发送消息
     */
    void send(SysMessageEntity entity);

    /**
     * 获取用户消息列表
     */
    List<SysMessageEntity> list(Long userId, Integer isRead);

    /**
     * 获取未读消息数
     */
    int countUnread(Long userId);

    /**
     * 标记已读
     */
    void markAsRead(Long id);

    /**
     * 标记全部已读
     */
    void markAllAsRead(Long userId);

    /**
     * 生成回访提醒消息
     */
    void generateVisitRemindMessages();
}
