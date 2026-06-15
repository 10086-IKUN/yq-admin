package cn.yanque.models.edu.schedule.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.schedule.mapper.EduClassScheduleMapper;
import cn.yanque.models.edu.schedule.pojo.entity.EduClassScheduleEntity;
import cn.yanque.models.edu.schedule.pojo.info.HolidayInfo;
import cn.yanque.models.edu.schedule.pojo.vo.req.ScheduleGenerateReq;
import cn.yanque.models.edu.schedule.pojo.vo.req.SchedulePageReq;
import cn.yanque.models.edu.schedule.pojo.vo.res.HolidayResponse;
import cn.yanque.models.edu.schedule.pojo.vo.res.ScheduleGenerateRes;
import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.edu.clazz.mapper.EduClassMapper;
import cn.yanque.models.edu.course.mapper.EduCourseDetailMapper;
import cn.yanque.models.edu.course.mapper.EduCourseMapper;
import cn.yanque.models.edu.clazz.pojo.entity.EduClassEntity;
import cn.yanque.models.edu.course.pojo.entity.EduCourseDetailEntity;
import cn.yanque.models.edu.course.pojo.entity.EduCourseEntity;
import cn.yanque.models.edu.schedule.service.EduScheduleService;
import cn.yanque.models.system.user.mapper.SysUserMapper;
import cn.yanque.models.system.user.pojo.entity.SysUserEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 班级服务实现类
 *
 * 核心优化：批量查询解决N+1查询问题
 * 优化前：10条数据 × 3次查询 = 30次数据库查询
 * 优化后：10条数据只需要3次批量查询
 */
@Service
@Slf4j
public class EduScheduleServiceImpl implements EduScheduleService {

    @Autowired
    private EduClassMapper eduClassMapper;

    @Autowired
    private EduCourseMapper eduCourseMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private EduClassScheduleMapper eduClassScheduleMapper;

    @Autowired
    private EduCourseDetailMapper eduCourseDetailMapper;

    /**
     * 生成班级课表
     *
     * 排课规则：
     * - 周日：休息（REST）
     * - 周四：自习（SELF_STUDY）
     * - 节假日：休息（REST）
     * - 周一、二、三、五、六：正式上课（CLASS）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleGenerateRes generateSchedule(Long classId, ScheduleGenerateReq req) {
        // 1. 查询班级信息
        EduClassEntity classEntity = eduClassMapper.selectById(classId);
        if (classEntity == null) {
            throw BusinessException.ClassNotExist;
        }

        // 2. 校验班级状态（待开班或授课中才能生成/重新生成课表）
        String classStatus = classEntity.getClassStatus();
        if (!"WAITING".equals(classStatus) && !"TEACHING".equals(classStatus)) {
            throw BusinessException.DataError.newInstance("只有待开班或授课中的班级才能生成课表");
        }

        // 3. 查询课程详情列表（按天数升序）
        List<EduCourseDetailEntity> courseDetails = eduCourseDetailMapper.selectByCourseId(classEntity.getCourseId());
        if (courseDetails.isEmpty()) {
            throw BusinessException.DataError.newInstance("该课程没有课程详情，请先添加课程详情");
        }

        // 4. 获取节假日数据（支持跨年）
        java.util.Map<String, HolidayInfo> holidayMap = fetchHolidayData(req.getStartDate(), courseDetails.size());

        // 5. 生成课表
        List<EduClassScheduleEntity> scheduleList = new java.util.ArrayList<>();
        int dayIndex = 0; // 课程详情索引
        java.util.Date currentDate = req.getStartDate();

        while (dayIndex < courseDetails.size()) {
            Week week = DateUtil.dayOfWeekEnum(currentDate);
            String dateKey = DateUtil.format(currentDate, "yyyy-MM-dd");

            EduClassScheduleEntity schedule = new EduClassScheduleEntity();
            schedule.setClassId(classId);
            schedule.setCourseId(classEntity.getCourseId());
            schedule.setScheduleDate(new java.sql.Date(currentDate.getTime()));

            // 判断日期类型
            if (week == Week.SUNDAY) {
                // 周日休息
                schedule.setScheduleType("REST");
                schedule.setCourseContent("休息");
            } else if (week == Week.THURSDAY) {
                // 周四自习
                schedule.setScheduleType("SELF_STUDY");
                schedule.setCourseContent("自习");
            } else if (holidayMap.containsKey(dateKey) && Boolean.TRUE.equals(holidayMap.get(dateKey).getHoliday())) {
                // 节假日休息
                schedule.setScheduleType("REST");
                schedule.setCourseContent("休息（" + holidayMap.get(dateKey).getName() + "）");
            } else {
                // 正式上课
                EduCourseDetailEntity detail = courseDetails.get(dayIndex);
                schedule.setScheduleType("CLASS");
                schedule.setCourseDetailId(detail.getId());
                schedule.setCourseDayNum(detail.getDayNum());
                schedule.setCourseContent(detail.getCourseContent());
                schedule.setStageName(detail.getStageName());
                // 根据阶段名称获取对应的老师ID
                Long teacherId = req.getStageTeachers().get(detail.getStageName());
                schedule.setTeacherId(teacherId);
                dayIndex++; // 只有正式上课才推进课程索引
            }

            schedule.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            schedule.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            scheduleList.add(schedule);

            // 日期加1
            currentDate = DateUtil.offsetDay(currentDate, 1);
        }

        // 6. 先删后插
        eduClassScheduleMapper.deleteByClassId(classId);
        if (!scheduleList.isEmpty()) {
            eduClassScheduleMapper.insertBatch(scheduleList);
        }

        // 7. 根据开班时间设置班级状态
        java.util.Date now = new java.util.Date();
        if (req.getStartDate().after(now)) {
            // 开班时间在未来，设置为待开班状态
            classEntity.setClassStatus("WAITING");
        } else if ("WAITING".equals(classStatus)) {
            // 原来是待开班，现在开班时间已到，设置为授课中
            classEntity.setClassStatus("TEACHING");
        }
        // 如果原来是授课中，重新生成后保持授课中状态
        classEntity.setUpdatedAt(now);
        eduClassMapper.updateById(classEntity);

        // 8. 返回结果
        ScheduleGenerateRes res = new ScheduleGenerateRes();
        res.setCount(scheduleList.size());
        return res;
    }

    /**
     * 分页查询班级课表
     */
    @Override
    public PageResult<SchedulePageRes> pageSchedule(SchedulePageReq req) {
        // 1. 分页查询课表
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<EduClassScheduleEntity> list = eduClassScheduleMapper.selectPage(
                req.getClassId(),
                req.getScheduleType(),
                req.getStartDate(),
                req.getEndDate()
        );
        PageInfo<EduClassScheduleEntity> pageInfo = new PageInfo<>(list);

        // 2. 批量查询关联数据
        Map<Long, String> classMap = batchGetClassNames(list);
        Map<Long, String> courseMap = batchGetCourseNamesBySchedule(list);
        Map<Long, String> userMap = batchGetUserNamesBySchedule(list);

        // 3. 转换为VO
        List<SchedulePageRes> records = list.stream()
                .map(entity -> buildSchedulePageRes(entity, classMap, courseMap, userMap))
                .toList();

        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    /**
     * 从timor.tech获取节假日数据（支持跨年）
     *
     * @param startDate 开始时间
     * @param days 总天数
     * @return 节假日Map，key为"年份-MM-dd"格式
     */
    private java.util.Map<String, HolidayInfo> fetchHolidayData(java.util.Date startDate, int days) {
        java.util.Map<String, HolidayInfo> result = new java.util.HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 计算结束时间
            java.util.Date endDate = DateUtil.offsetDay(startDate, days + 30);

            int startYear = DateUtil.year(startDate);
            int endYear = DateUtil.year(endDate);

            // 获取每一年的节假日数据
            for (int year = startYear; year <= endYear; year++) {
                String url = "https://timor.tech/api/holiday/year/" + year;
                HolidayResponse response = restTemplate.getForObject(url, HolidayResponse.class);

                if (response != null && response.getHoliday() != null) {
                    // 将 key 从 "MM-dd" 转换为 "yyyy-MM-dd"
                    for (java.util.Map.Entry<String, HolidayInfo> entry : response.getHoliday().entrySet()) {
                        String fullDateKey = year + "-" + entry.getKey();
                        result.put(fullDateKey, entry.getValue());
                    }
                }
            }
        } catch (Exception e) {
            // 节假日API调用失败，不影响排课（所有日期按工作日处理）
            log.warn("调用节假日API失败: {}", e.getMessage());
        }

        return result;
    }

    /**
     * 批量获取班级名称
     */
    private Map<Long, String> batchGetClassNames(List<EduClassScheduleEntity> scheduleList) {
        Set<Long> classIds = scheduleList.stream()
                .map(EduClassScheduleEntity::getClassId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (classIds.isEmpty()) {
            return Map.of();
        }

        List<EduClassEntity> classes = eduClassMapper.selectByIds(classIds);
        return classes.stream()
                .collect(Collectors.toMap(EduClassEntity::getId, c -> "第" + c.getClassTerm() + "期"));
    }

    /**
     * 批量获取课程名称（按课表）
     */
    private Map<Long, String> batchGetCourseNamesBySchedule(List<EduClassScheduleEntity> scheduleList) {
        Set<Long> courseIds = scheduleList.stream()
                .map(EduClassScheduleEntity::getCourseId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (courseIds.isEmpty()) {
            return Map.of();
        }

        List<EduCourseEntity> courses = eduCourseMapper.selectByIds(courseIds);
        return courses.stream()
                .collect(Collectors.toMap(EduCourseEntity::getId, EduCourseEntity::getCourseName));
    }

    /**
     * 批量获取用户名称（按课表）
     */
    private Map<Long, String> batchGetUserNamesBySchedule(List<EduClassScheduleEntity> scheduleList) {
        Set<Long> userIds = scheduleList.stream()
                .map(EduClassScheduleEntity::getTeacherId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (userIds.isEmpty()) {
            return Map.of();
        }

        List<SysUserEntity> users = sysUserMapper.selectByIds(userIds);
        return users.stream()
                .collect(Collectors.toMap(SysUserEntity::getId, SysUserEntity::getNickname));
    }

    /**
     * 构建课表分页响应对象
     */
    private SchedulePageRes buildSchedulePageRes(EduClassScheduleEntity entity,
                                                  Map<Long, String> classMap,
                                                  Map<Long, String> courseMap,
                                                  Map<Long, String> userMap) {
        SchedulePageRes res = new SchedulePageRes();
        BeanUtils.copyProperties(entity, res);

        if (entity.getClassId() != null) {
            res.setClassName(classMap.get(entity.getClassId()));
        }
        if (entity.getCourseId() != null) {
            res.setCourseName(courseMap.get(entity.getCourseId()));
        }
        if (entity.getTeacherId() != null) {
            res.setTeacherName(userMap.get(entity.getTeacherId()));
        }

        return res;
    }

    @Override
    public List<Long> getBusyTeacherIds(Long classId, Date startDate, Date endDate) {
        return eduClassScheduleMapper.selectBusyTeacherIds(startDate, endDate, classId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(Long scheduleId) {
        EduClassScheduleEntity entity = eduClassScheduleMapper.selectById(scheduleId);
        if (entity == null) {
            throw BusinessException.DataError.newInstance("课表记录不存在");
        }
        Long classId = entity.getClassId();

        // 如果是 CLASS 记录，需要处理后续记录的前移和重排序
        if ("CLASS".equals(entity.getScheduleType()) && entity.getCourseDayNum() != null) {
            Integer deletedDayNum = entity.getCourseDayNum();
            java.sql.Date deletedDate = entity.getScheduleDate();

            // 获取节假日数据
            List<EduClassScheduleEntity> allRecords = eduClassScheduleMapper.selectAllByClassId(classId);
            Date earliestDate = allRecords.get(0).getScheduleDate();
            Date latestDate = allRecords.get(allRecords.size() - 1).getScheduleDate();
            int totalDays = (int) ((latestDate.getTime() - earliestDate.getTime()) / (1000 * 60 * 60 * 24)) + 60;
            Map<String, HolidayInfo> holidayMap = fetchHolidayData(earliestDate, totalDays);

            // 找到所有需要前移的 CLASS 记录（day_num > deletedDayNum）
            List<EduClassScheduleEntity> toShift = allRecords.stream()
                    .filter(r -> "CLASS".equals(r.getScheduleType())
                            && r.getCourseDayNum() != null
                            && r.getCourseDayNum() > deletedDayNum
                            && !r.getId().equals(scheduleId))
                    .sorted(Comparator.comparing(EduClassScheduleEntity::getCourseDayNum))
                    .collect(java.util.stream.Collectors.toList());

            // 为每条记录找前一个可用的上课日
            for (EduClassScheduleEntity record : toShift) {
                Date prevDate = DateUtil.offsetDay(record.getScheduleDate(), -1);
                while (true) {
                    // 不能早于被删除记录的日期
                    if (prevDate.before(deletedDate)) {
                        break;
                    }
                    String dateKey = DateUtil.format(prevDate, "yyyy-MM-dd");
                    Week week = DateUtil.dayOfWeekEnum(prevDate);
                    boolean isSunday = (week == Week.SUNDAY);
                    boolean isThursday = (week == Week.THURSDAY);
                    boolean isHoliday = holidayMap.containsKey(dateKey) && Boolean.TRUE.equals(holidayMap.get(dateKey).getHoliday());

                    if (!isSunday && !isThursday && !isHoliday) {
                        // 找到上课日，更新日期
                        record.setScheduleDate(new java.sql.Date(prevDate.getTime()));
                        break;
                    }
                    prevDate = DateUtil.offsetDay(prevDate, -1);
                }
                // day_num - 1
                record.setCourseDayNum(record.getCourseDayNum() - 1);
                record.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                eduClassScheduleMapper.updateById(record);
            }
        }

        // 删除记录
        eduClassScheduleMapper.deleteById(scheduleId);
    }

    @Override
    public List<Long> getBusyTeacherIdsByDate(Date scheduleDate) {
        return eduClassScheduleMapper.selectBusyTeacherIdsByDate(scheduleDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSchedule(EduClassScheduleEntity entity, Date scheduleDate) {
        Long classId = entity.getClassId();

        // 1. 查询该班级所有课程记录
        List<EduClassScheduleEntity> allRecords = eduClassScheduleMapper.selectAllByClassId(classId);
        if (allRecords.isEmpty()) {
            throw BusinessException.DataError.newInstance("该班级没有课表记录");
        }

        final java.sql.Date insertDate = new java.sql.Date(scheduleDate.getTime());

        // 2. 查找目标日期的 CLASS 记录
        EduClassScheduleEntity targetRecord = allRecords.stream()
                .filter(r -> r.getScheduleDate().equals(insertDate) && "CLASS".equals(r.getScheduleType()))
                .findFirst()
                .orElse(null);

        // 3. 获取节假日数据
        Date earliestDate = allRecords.get(0).getScheduleDate();
        Date latestDate = DateUtil.offsetDay(scheduleDate, 365);
        int totalDays = (int) ((latestDate.getTime() - earliestDate.getTime()) / (1000 * 60 * 60 * 24)) + 60;
        Map<String, HolidayInfo> holidayMap = fetchHolidayData(earliestDate, totalDays);

        // 4. 设置新课程信息
        entity.setScheduleDate(insertDate);
        if (entity.getScheduleType() == null) {
            entity.setScheduleType("CLASS");
        }
        entity.setCourseDetailId(null);
        entity.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        entity.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        if ("CLASS".equals(entity.getScheduleType())) {
            if (targetRecord != null) {
                // === 插入模式：在 CLASS 记录前插入，原记录后移 ===
                entity.setCourseDayNum(targetRecord.getCourseDayNum());

                // 找到所有需要移动的 CLASS 记录
                Integer targetDayNum = targetRecord.getCourseDayNum();
                List<EduClassScheduleEntity> toShift = allRecords.stream()
                        .filter(r -> "CLASS".equals(r.getScheduleType()) && r.getCourseDayNum() != null && r.getCourseDayNum() >= targetDayNum)
                        .sorted(Comparator.comparing(EduClassScheduleEntity::getCourseDayNum))
                        .collect(java.util.stream.Collectors.toList());

                // 为需要移动的记录找新的日期
                for (EduClassScheduleEntity record : toShift) {
                    Date nextDate = DateUtil.offsetDay(record.getScheduleDate(), 1);
                    while (true) {
                        String dateKey = DateUtil.format(nextDate, "yyyy-MM-dd");
                        Week week = DateUtil.dayOfWeekEnum(nextDate);
                        boolean isSunday = (week == Week.SUNDAY);
                        boolean isThursday = (week == Week.THURSDAY);
                        boolean isHoliday = holidayMap.containsKey(dateKey) && Boolean.TRUE.equals(holidayMap.get(dateKey).getHoliday());

                        if (!isSunday && !isThursday && !isHoliday) {
                            record.setScheduleDate(new java.sql.Date(nextDate.getTime()));
                            break;
                        }
                        nextDate = DateUtil.offsetDay(nextDate, 1);
                    }
                }

                // 更新 day_num
                for (EduClassScheduleEntity record : toShift) {
                    record.setCourseDayNum(record.getCourseDayNum() + 1);
                    record.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                }
            } else {
                // === 新增模式：在空白日期添加新课程 ===
                // 计算 day_num：找到前一个 CLASS 记录的 day_num + 1
                Integer newDayNum = 1;
                List<EduClassScheduleEntity> sortedRecords = allRecords.stream()
                        .filter(r -> "CLASS".equals(r.getScheduleType()) && r.getCourseDayNum() != null)
                        .sorted(Comparator.comparing(EduClassScheduleEntity::getCourseDayNum))
                        .collect(java.util.stream.Collectors.toList());

                for (EduClassScheduleEntity record : sortedRecords) {
                    if (record.getScheduleDate().before(insertDate)) {
                        newDayNum = record.getCourseDayNum() + 1;
                    } else {
                        break;
                    }
                }
                entity.setCourseDayNum(newDayNum);

                // 后续 CLASS 记录 day_num + 1
                for (EduClassScheduleEntity record : sortedRecords) {
                    if (record.getCourseDayNum() >= newDayNum) {
                        record.setCourseDayNum(record.getCourseDayNum() + 1);
                        record.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                        eduClassScheduleMapper.updateById(record);
                    }
                }
            }
        }
        // REST/SELF_STUDY 类型：不需要设置 day_num，也不需要移动其他记录

        // 5. 添加新课程到列表
        allRecords.add(entity);

        // 6. 按日期排序
        allRecords.sort(Comparator.comparing(EduClassScheduleEntity::getScheduleDate));

        // 7. 删除旧记录，插入新记录
        eduClassScheduleMapper.deleteByClassId(classId);
        if (!allRecords.isEmpty()) {
            eduClassScheduleMapper.insertBatch(allRecords);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSchedule(Long scheduleId, String scheduleType, String courseContent, Long teacherId, String stageName) {
        EduClassScheduleEntity entity = eduClassScheduleMapper.selectById(scheduleId);
        if (entity == null) {
            throw BusinessException.DataError.newInstance("课表记录不存在");
        }

        String oldType = entity.getScheduleType();
        Long classId = entity.getClassId();

        // 更新基本字段
        if (scheduleType != null) {
            entity.setScheduleType(scheduleType);
        }
        if (courseContent != null) {
            entity.setCourseContent(courseContent);
        }
        if (teacherId != null) {
            entity.setTeacherId(teacherId);
        }
        if (stageName != null) {
            entity.setStageName(stageName);
        }

        // 处理类型变化和 day_num 重排序
        if (scheduleType != null && !scheduleType.equals(oldType)) {
            List<EduClassScheduleEntity> allRecords = eduClassScheduleMapper.selectAllByClassId(classId);

            if ("CLASS".equals(oldType) && !"CLASS".equals(scheduleType)) {
                // CLASS → REST/SELF_STUDY：清空当前记录的 day_num，后续 CLASS 记录 day_num - 1
                Integer oldDayNum = entity.getCourseDayNum(); // 先保存旧值
                entity.setCourseDayNum(null);
                entity.setClearCourseDayNum(true); // 标记需要清空数据库中的值
                entity.setTeacherId(null);
                entity.setClearTeacherId(true);
                entity.setStageName(null);
                entity.setClearStageName(true);

                // 后续 CLASS 记录 day_num - 1
                if (oldDayNum != null) {
                    for (EduClassScheduleEntity record : allRecords) {
                        if ("CLASS".equals(record.getScheduleType())
                                && record.getCourseDayNum() != null
                                && record.getCourseDayNum() > oldDayNum
                                && !record.getId().equals(scheduleId)) {
                            record.setCourseDayNum(record.getCourseDayNum() - 1);
                            record.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                            eduClassScheduleMapper.updateById(record);
                        }
                    }
                }
            } else if (!"CLASS".equals(oldType) && "CLASS".equals(scheduleType)) {
                // REST/SELF_STUDY → CLASS：找到插入位置，设置 day_num，后续 CLASS 记录 day_num + 1
                entity.setTeacherId(teacherId);
                entity.setStageName(stageName);

                // 找到插入位置（按日期排序，找到当前记录的位置）
                List<EduClassScheduleEntity> sortedRecords = allRecords.stream()
                        .sorted(Comparator.comparing(EduClassScheduleEntity::getScheduleDate))
                        .collect(java.util.stream.Collectors.toList());

                int position = -1;
                for (int i = 0; i < sortedRecords.size(); i++) {
                    if (sortedRecords.get(i).getId().equals(scheduleId)) {
                        position = i;
                        break;
                    }
                }

                // 计算新的 day_num：前一个 CLASS 记录的 day_num + 1
                Integer newDayNum = 1;
                for (int i = position - 1; i >= 0; i--) {
                    if ("CLASS".equals(sortedRecords.get(i).getScheduleType())
                            && sortedRecords.get(i).getCourseDayNum() != null) {
                        newDayNum = sortedRecords.get(i).getCourseDayNum() + 1;
                        break;
                    }
                }
                entity.setCourseDayNum(newDayNum);

                // 后续 CLASS 记录 day_num + 1
                for (EduClassScheduleEntity record : allRecords) {
                    if ("CLASS".equals(record.getScheduleType())
                            && record.getCourseDayNum() != null
                            && record.getCourseDayNum() >= newDayNum
                            && !record.getId().equals(scheduleId)) {
                        record.setCourseDayNum(record.getCourseDayNum() + 1);
                        record.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                        eduClassScheduleMapper.updateById(record);
                    }
                }
            }
        }

        entity.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        eduClassScheduleMapper.updateById(entity);
    }

}
