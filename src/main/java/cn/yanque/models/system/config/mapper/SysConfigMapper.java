package cn.yanque.models.system.config.mapper;

import cn.yanque.models.system.config.pojo.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysConfigMapper {

    int insert(SysConfigEntity entity);

    int updateById(SysConfigEntity entity);

    SysConfigEntity selectById(@Param("id") Long id);

    SysConfigEntity selectByKey(@Param("k") String k);

    List<SysConfigEntity> selectAll();

    int deleteById(@Param("id") Long id);
}
