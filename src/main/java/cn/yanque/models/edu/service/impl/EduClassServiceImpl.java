package cn.yanque.models.edu.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.common.pojo.info.HolidayInfo;
import cn.yanque.common.pojo.vo.res.*;
import cn.yanque.models.edu.mapper.EduCampusMapper;
import cn.yanque.models.edu.mapper.EduClassMapper;
import cn.yanque.models.edu.mapper.EduClassScheduleMapper;
import cn.yanque.models.edu.mapper.EduCourseDetailMapper;
import cn.yanque.models.edu.mapper.EduCourseMapper;
import cn.yanque.common.pojo.entity.EduCampusEntity;
import cn.yanque.common.pojo.entity.EduClassEntity;
import cn.yanque.common.pojo.entity.EduClassScheduleEntity;
import cn.yanque.common.pojo.entity.EduCourseDetailEntity;
import cn.yanque.common.pojo.entity.EduCourseEntity;
import cn.yanque.common.pojo.vo.req.ClassCreateReq;
import cn.yanque.common.pojo.vo.req.ClassPageReq;
import cn.yanque.common.pojo.vo.req.ClassUpdateReq;
import cn.yanque.common.pojo.vo.req.ScheduleGenerateReq;
import cn.yanque.common.pojo.vo.req.SchedulePageReq;
import cn.yanque.models.edu.service.EduClassService;
import cn.yanque.models.users.mapper.SysUserMapper;
import cn.yanque.common.pojo.entity.SysUserEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class EduClassServiceImpl implements EduClassService {

    @Autowired
    private EduClassMapper eduClassMapper;

    @Autowired
    private EduCampusMapper eduCampusMapper;

    @Autowired
    private EduCourseMapper eduCourseMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private EduClassScheduleMapper eduClassScheduleMapper;

    @Autowired
    private EduCourseDetailMapper eduCourseDetailMapper;

    /**
     * 添加班级
     * @param req 创建班级请求参数
     * @return 创建成功的班级ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassCreateRes addClass(ClassCreateReq req) {
        EduClassEntity entity = new EduClassEntity();
        entity.setClassTerm(req.getClassTerm());
        entity.setCampusId(req.getCampusId());
        entity.setHeadTeacherId(req.getHeadTeacherId());
        entity.setClassStatus(req.getClassStatus());
        entity.setStartTime(req.getStartTime());
        entity.setCourseId(req.getCourseId());
        entity.setStudentCount(0);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        eduClassMapper.insert(entity);

        ClassCreateRes res = new ClassCreateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 修改班级
     * @param req 更新班级请求参数
     * @return 更新后的班级ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassUpdateRes updateClass(ClassUpdateReq req) {
        EduClassEntity entity = eduClassMapper.selectById(req.getId());
        if (entity == null) {
            throw BusinessException.ClassNotExist;
        }

        entity.setClassTerm(req.getClassTerm());
        entity.setCampusId(req.getCampusId());
        entity.setHeadTeacherId(req.getHeadTeacherId());
        entity.setClassStatus(req.getClassStatus());
        entity.setStartTime(req.getStartTime());
        entity.setCourseId(req.getCourseId());
        entity.setUpdatedAt(new Date());

        int rows = eduClassMapper.updateById(entity);
        if (rows == 0) {
            throw BusinessException.ClassNotExist;
        }

        ClassUpdateRes res = new ClassUpdateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 删除班级
     * @param id 班级ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassDeleteRes deleteClass(Long id) {
        EduClassEntity entity = eduClassMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.ClassNotExist;
        }

        int rows = eduClassMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.ClassNotExist;
        }

        ClassDeleteRes res = new ClassDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 根据ID查询班级详情
     * @param id 班级ID
     * @return 班级详细信息（含校区、课程、班主任名称）
     */
    @Override
    public ClassDetailRes getClassById(Long id) {
        EduClassEntity entity = eduClassMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.ClassNotExist;
        }
        return buildClassDetailRes(entity);
    }

    /**
     * 分页查询班级（优化版：批量查询关联名称）
     *
     * 优化前（N+1问题）：
     * - 10条数据需要 10×3=30次数据库查询
     *
     * 优化后（批量查询）：
     * - 10条数据只需要 3次数据库查询
     *
     * @param req 分页查询参数（关键词、状态、校区ID）
     * @return 分页班级列表
     */
    @Override
    public PageResult<ClassPageRes> pageClass(ClassPageReq req) {
        // 1. 分页查询班级列表
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<EduClassEntity> list = eduClassMapper.selectPage(
                req.getKeyword(),
                req.getClassStatus(),
                req.getCampusId()
        );
        PageInfo<EduClassEntity> pageInfo = new PageInfo<>(list);

        // 2. 批量查询关联数据（核心优化）
        Map<Long, String> campusMap = batchGetCampusNames(list);
        Map<Long, String> courseMap = batchGetCourseNames(list);
        Map<Long, String> userMap = batchGetUserNames(list);

        // 3. 转换为VO并填充关联名称
        List<ClassPageRes> records = list.stream()
                .map(entity -> buildClassPageRes(entity, campusMap, courseMap, userMap))
                .toList();

        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    /**
     * 批量获取校区名称（优化：1次查询代替N次）
     *
     * 执行流程：
     * 1. 从班级列表中提取所有不重复的campusId
     * 2. 一次性查询所有校区
     * 3. 转换为 Map<校区ID, 校区名称>
     *
     * @param classList 班级列表
     * @return 校区ID -> 校区名称 的映射
     */
    private Map<Long, String> batchGetCampusNames(List<EduClassEntity> classList) {
        // 1. 收集所有不重复的campusId（过滤null）
        Set<Long> campusIds = classList.stream()
                .map(EduClassEntity::getCampusId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // 2. 如果没有需要查询的ID，返回空Map
        if (campusIds.isEmpty()) {
            return Map.of();
        }

        // 3. 批量查询（1次SQL查询）
        List<EduCampusEntity> campuses = eduCampusMapper.selectByIds(campusIds);

        // 4. 转换为 Map<ID, 名称>
        return campuses.stream()
                .collect(Collectors.toMap(EduCampusEntity::getId, EduCampusEntity::getCampusName));
    }

    /**
     * 批量获取课程名称（优化：1次查询代替N次）
     */
    private Map<Long, String> batchGetCourseNames(List<EduClassEntity> classList) {
        Set<Long> courseIds = classList.stream()
                .map(EduClassEntity::getCourseId)
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
     * 批量获取班主任昵称（优化：1次查询代替N次）
     */
    private Map<Long, String> batchGetUserNames(List<EduClassEntity> classList) {
        Set<Long> teacherIds = classList.stream()
                .map(EduClassEntity::getHeadTeacherId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (teacherIds.isEmpty()) {
            return Map.of();
        }

        List<SysUserEntity> users = sysUserMapper.selectByIds(teacherIds);

        return users.stream()
                .collect(Collectors.toMap(SysUserEntity::getId, SysUserEntity::getNickname));
    }

    /**
     * 构建班级详情响应对象（单条查询，使用原方法）
     * @param entity 班级实体
     * @return 包含关联名称的班级详情
     */
    private ClassDetailRes buildClassDetailRes(EduClassEntity entity) {
        ClassDetailRes res = new ClassDetailRes();
        BeanUtils.copyProperties(entity, res);
        fillRelatedNames(entity.getCampusId(), entity.getCourseId(), entity.getHeadTeacherId(),
                res::setCampusName, res::setCourseName, res::setHeadTeacherName);
        return res;
    }

    /**
     * 构建班级分页响应对象（批量查询版）
     *
     * 优化点：直接从Map中获取名称，不再查询数据库
     *
     * @param entity 班级实体
     * @param campusMap 校区ID -> 校区名称
     * @param courseMap 课程ID -> 课程名称
     * @param userMap 用户ID -> 用户昵称
     * @return 包含关联名称的班级分页信息
     */
    private ClassPageRes buildClassPageRes(EduClassEntity entity,
                                           Map<Long, String> campusMap,
                                           Map<Long, String> courseMap,
                                           Map<Long, String> userMap) {
        ClassPageRes res = new ClassPageRes();
        BeanUtils.copyProperties(entity, res);

        // 从Map中获取名称（O(1)时间复杂度）
        if (entity.getCampusId() != null) {
            res.setCampusName(campusMap.get(entity.getCampusId()));
        }
        if (entity.getCourseId() != null) {
            res.setCourseName(courseMap.get(entity.getCourseId()));
        }
        if (entity.getHeadTeacherId() != null) {
            res.setHeadTeacherName(userMap.get(entity.getHeadTeacherId()));
        }

        return res;
    }

    /**
     * 填充关联实体的名称字段（用于单条查询场景）
     *
     * 使用Consumer函数式接口，避免重复代码
     *
     * @param campusId 校区ID
     * @param courseId 课程ID
     * @param headTeacherId 班主任用户ID
     * @param setCampusName 校区名称设置器
     * @param setCourseName 课程名称设置器
     * @param setHeadTeacherName 班主任昵称设置器
     */
    private void fillRelatedNames(Long campusId, Long courseId, Long headTeacherId,
                                  java.util.function.Consumer<String> setCampusName,
                                  java.util.function.Consumer<String> setCourseName,
                                  java.util.function.Consumer<String> setHeadTeacherName) {
        if (campusId != null) {
            EduCampusEntity campus = eduCampusMapper.selectById(campusId);
            if (campus != null) setCampusName.accept(campus.getCampusName());
        }
        if (courseId != null) {
            EduCourseEntity course = eduCourseMapper.selectById(courseId);
            if (course != null) setCourseName.accept(course.getCourseName());
        }
        if (headTeacherId != null) {
            SysUserEntity user = sysUserMapper.selectById(headTeacherId);
            if (user != null) setHeadTeacherName.accept(user.getNickname());
        }
    }

    /**
     * 生成班级课表
     *
     * 排课规则：
     * - 周日：休息（REST）
     * - 周四：自习（SELF_STUDY）
     * - 节假日：休息（REST）
     * - 周一、二、三、五、六：正式上课（CLASS）
     *
     * @param classId 班级ID
     * @param req 开班时间、授课老师ID
     * @return 生成的课表记录数
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

}
