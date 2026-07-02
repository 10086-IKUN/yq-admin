package cn.yanque.models.system.config.mapper;

import cn.yanque.models.system.config.pojo.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * SysConfigMapper 数据访问接口。
 *
 * <p>由 MyBatis 根据 XML 或注解实现，集中封装对应业务表的查询和更新操作。</p>
 */
public interface SysConfigMapper {

    int insert(SysConfigEntity entity);

    int updateById(SysConfigEntity entity);

    SysConfigEntity selectById(@Param("id") Long id);

    SysConfigEntity selectByKey(@Param("k") String k);

    List<SysConfigEntity> selectAll();

    int deleteById(@Param("id") Long id);
}
