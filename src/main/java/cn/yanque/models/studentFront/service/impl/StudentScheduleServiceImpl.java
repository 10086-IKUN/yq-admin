package cn.yanque.models.studentFront.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.clazz.mapper.EduClassMapper;
import cn.yanque.models.edu.clazz.pojo.entity.EduClassEntity;
import cn.yanque.models.edu.course.mapper.EduCourseMapper;
import cn.yanque.models.edu.course.pojo.entity.EduCourseEntity;
import cn.yanque.models.edu.schedule.mapper.EduClassScheduleMapper;
import cn.yanque.models.edu.schedule.pojo.entity.EduClassScheduleEntity;
import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.service.StudentScheduleService;
import cn.yanque.models.system.user.mapper.SysUserMapper;
import cn.yanque.models.system.user.pojo.entity.SysUserEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学员端课表服务实现类。
 *
 * <p>学生端课表不能由前端传 classId 查询。正确流程是：</p>
 * <ol>
 *     <li>用登录 token 中的 studentId 查询学生。</li>
 *     <li>从学生信息中拿到绑定的 classId。</li>
 *     <li>只查询这个班级的课表。</li>
 *     <li>补齐班级名、课程名、老师名，方便前端直接展示。</li>
 * </ol>
 */
@Service
public class StudentScheduleServiceImpl implements StudentScheduleService {

    @Autowired
    private EduClassScheduleMapper eduClassScheduleMapper;

    @Autowired
    private EduStudentMapper eduStudentMapper;

    @Autowired
    private EduClassMapper eduClassMapper;

    @Autowired
    private EduCourseMapper eduCourseMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取当前登录学员的个人课表。
     *
     * <p>如果学员没有绑定班级，说明系统暂时无法确定应该展示哪一份课表，
     * 这里返回空列表，让前端显示“暂无课表安排”。</p>
     *
     * @param studentId 当前登录学员ID，来自学员端 token
     * @return 当前学员所在班级的课表列表
     */
    @Override
    public List<SchedulePageRes> list(Long studentId) {
        EduStudentEntity student = eduStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(404, "学员不存在");
        }
        if (student.getClassId() == null) {
            return List.of();
        }

        // 学员端只能查看自己绑定班级的课表，避免看到其他班级的安排。
        List<EduClassScheduleEntity> scheduleList = eduClassScheduleMapper.selectPage(student.getClassId(), null, null, null);
        if (scheduleList.isEmpty()) {
            return List.of();
        }

        EduClassEntity classEntity = eduClassMapper.selectById(student.getClassId());
        Map<Long, String> courseMap = batchGetCourseNames(scheduleList);
        Map<Long, String> teacherMap = batchGetTeacherNames(scheduleList);

        return scheduleList.stream()
                .map(entity -> buildSchedulePageRes(entity, classEntity, courseMap, teacherMap))
                .toList();
    }

    /**
     * 批量查询课程名称。
     *
     * <p>课表列表里只保存 courseId，学生端需要显示“JAVA+AI课程”这种可读名称。
     * 这里一次性查出所有课程，避免循环里一条一条查询数据库。</p>
     *
     * @param scheduleList 当前学生所在班级的课表记录
     * @return 课程ID到课程名称的映射
     */
    private Map<Long, String> batchGetCourseNames(List<EduClassScheduleEntity> scheduleList) {
        Set<Long> courseIds = scheduleList.stream()
                .map(EduClassScheduleEntity::getCourseId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (courseIds.isEmpty()) {
            return Map.of();
        }

        return eduCourseMapper.selectByIds(courseIds).stream()
                .collect(Collectors.toMap(EduCourseEntity::getId, EduCourseEntity::getCourseName, (first, second) -> first));
    }

    /**
     * 批量查询授课老师名称。
     *
     * <p>老师信息来自系统用户表，课表里只保存 teacherId。
     * 统一补齐 teacherName 后，首页“今日安排”和课表页都可以直接展示老师姓名。</p>
     *
     * @param scheduleList 当前学生所在班级的课表记录
     * @return 老师用户ID到老师昵称的映射
     */
    private Map<Long, String> batchGetTeacherNames(List<EduClassScheduleEntity> scheduleList) {
        Set<Long> teacherIds = scheduleList.stream()
                .map(EduClassScheduleEntity::getTeacherId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (teacherIds.isEmpty()) {
            return Map.of();
        }

        return sysUserMapper.selectByIds(teacherIds).stream()
                .collect(Collectors.toMap(SysUserEntity::getId, SysUserEntity::getNickname, (first, second) -> first));
    }

    /**
     * 组装学生端课表响应。
     *
     * <p>数据库实体只负责保存基础字段。这里把班级名、课程名和老师名补齐，
     * 前端就不用再根据 ID 做二次查询。</p>
     *
     * @param entity 课表数据库记录
     * @param classEntity 当前学生所在班级
     * @param courseMap 课程ID到课程名称的映射
     * @param teacherMap 老师用户ID到老师昵称的映射
     * @return 学生端可直接展示的课表响应对象
     */
    private SchedulePageRes buildSchedulePageRes(EduClassScheduleEntity entity,
                                                 EduClassEntity classEntity,
                                                 Map<Long, String> courseMap,
                                                 Map<Long, String> teacherMap) {
        SchedulePageRes res = new SchedulePageRes();
        BeanUtils.copyProperties(entity, res);

        if (classEntity != null && classEntity.getClassTerm() != null) {
            res.setClassName("第" + classEntity.getClassTerm() + "期");
        }
        if (entity.getCourseId() != null) {
            res.setCourseName(courseMap.get(entity.getCourseId()));
        }
        if (entity.getTeacherId() != null) {
            res.setTeacherName(teacherMap.get(entity.getTeacherId()));
        }

        return res;
    }
}
