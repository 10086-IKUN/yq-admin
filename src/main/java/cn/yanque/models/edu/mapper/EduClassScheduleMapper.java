package cn.yanque.models.edu.mapper;

import cn.yanque.common.pojo.entity.EduClassScheduleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 班级课表Mapper接口
 */
public interface EduClassScheduleMapper {

    /**
     * 插入单条记录
     */
    void insert(EduClassScheduleEntity entity);

    /**
     * 批量插入
     */
    void insertBatch(@Param("list") List<EduClassScheduleEntity> list);

    /**
     * 根据班级ID删除所有课表
     */
    int deleteByClassId(@Param("classId") Long classId);

    /**
     * 根据ID查询
     */
    EduClassScheduleEntity selectById(@Param("id") Long id);

    /**
     * 分页查询
     */
    List<EduClassScheduleEntity> selectPage(@Param("classId") Long classId,
                                             @Param("scheduleType") String scheduleType,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);

    /**
     * 查询指定日期范围内已排课的老师ID（排除当前班级）
     */
    List<Long> selectBusyTeacherIds(@Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate,
                                     @Param("excludeClassId") Long excludeClassId);

    /**
     * 更新单条课表记录
     */
    int updateById(EduClassScheduleEntity entity);

    /**
     * 删除单条课表记录
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询班级所有CLASS类型课程（按日期升序，用于重排序）
     */
    List<EduClassScheduleEntity> selectClassDaysByClassId(@Param("classId") Long classId);

    /**
     * 查询班级所有课程记录（按日期升序）
     */
    List<EduClassScheduleEntity> selectAllByClassId(@Param("classId") Long classId);

    /**
     * 批量更新courseDayNum（重排序用）
     */
    void batchUpdateDayNum(@Param("list") List<EduClassScheduleEntity> list);

    /**
     * 查询指定日期已排课的老师ID（包含当前班级，用于修改老师时筛选）
     */
    List<Long> selectBusyTeacherIdsByDate(@Param("scheduleDate") Date scheduleDate);
}
