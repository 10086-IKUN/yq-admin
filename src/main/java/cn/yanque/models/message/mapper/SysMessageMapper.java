package cn.yanque.models.message.mapper;

import cn.yanque.models.message.pojo.entity.SysMessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMessageMapper {

    void insert(SysMessageEntity entity);

    List<SysMessageEntity> selectByUserId(@Param("userId") Long userId,
                                           @Param("isRead") Integer isRead);

    int countUnread(@Param("userId") Long userId);

    void markAsRead(@Param("id") Long id);

    void markAllAsRead(@Param("userId") Long userId);
}
