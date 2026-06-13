package cn.yanque.models.duty.mapper;

import cn.yanque.common.pojo.entity.EduDutyAssignmentEntity;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

/**
 * 值班安排Mapper接口
 */
public interface EduDutyAssignmentMapper {

    void insert(EduDutyAssignmentEntity entity);

    int updateById(EduDutyAssignmentEntity entity);

    EduDutyAssignmentEntity selectById(@Param("id") Long id);

    List<EduDutyAssignmentEntity> selectPage(@Param("classId") Long classId,
                                              @Param("dutyType") String dutyType,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate);

    int deleteById(@Param("id") Long id);

    int countByDateAndTypeAndClass(@Param("dutyDate") Date dutyDate,
                                    @Param("dutyType") String dutyType,
                                    @Param("classId") Long classId,
                                    @Param("excludeId") Long excludeId);
}
