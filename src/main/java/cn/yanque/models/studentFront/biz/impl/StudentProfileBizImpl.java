package cn.yanque.models.studentFront.biz.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.clazz.mapper.EduClassMapper;
import cn.yanque.models.edu.clazz.pojo.entity.EduClassEntity;
import cn.yanque.models.edu.course.mapper.EduCourseMapper;
import cn.yanque.models.edu.course.pojo.entity.EduCourseEntity;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.biz.StudentProfileBiz;
import cn.yanque.models.studentFront.pojo.vo.req.StudentProfileUpdateReq;
import cn.yanque.models.studentFront.pojo.vo.res.StudentProfileRes;
import cn.yanque.models.studentFront.service.StudentProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 学员端个人中心业务实现类。
 *
 * <p>这里不直接把 EduStudentEntity 原样返回给前端。
 * 学员首页需要的是“第几期”“课程名称”这种可读信息，
 * 而不是 classId、courseId 这种数据库字段，所以在业务层统一补齐展示字段。</p>
 */
@Component
public class StudentProfileBizImpl implements StudentProfileBiz {

    @Autowired
    private StudentProfileService studentProfileService;

    @Autowired
    private EduClassMapper eduClassMapper;

    @Autowired
    private EduCourseMapper eduCourseMapper;

    /**
     * 获取当前登录学员的个人信息。
     *
     * @param studentId 当前登录学员ID，来自学生端登录 token
     * @return 学员端个人信息响应
     */
    @Override
    public StudentProfileRes getProfile(Long studentId) {
        EduStudentEntity entity = studentProfileService.getById(studentId);
        if (entity == null) {
            throw new BusinessException(404, "学员不存在");
        }

        StudentProfileRes res = new StudentProfileRes();
        BeanUtils.copyProperties(entity, res);

        fillClassAndCourseInfo(entity, res);
        return res;
    }

    /**
     * 修改当前登录学员的个人信息。
     *
     * @param studentId 当前登录学员ID，来自学生端登录 token
     * @param req 前端提交的可编辑资料，只包含学员允许自己修改的字段
     */
    @Override
    public void updateProfile(Long studentId, StudentProfileUpdateReq req) {
        EduStudentEntity entity = new EduStudentEntity();
        entity.setId(studentId);
        entity.setStudentName(req.getStudentName());
        entity.setSchool(req.getSchool());
        entity.setEducation(req.getEducation());
        studentProfileService.update(entity);
    }

    /**
     * 给学员信息补充班级和课程展示字段。
     *
     * <p>学员表只保存 classId。
     * 首页需要显示“第5期”“JAVA+AI课程”这种给学生看的信息，
     * 所以这里通过班级表找到 classTerm 和 courseId，
     * 再通过课程表找到 courseName。</p>
     *
     * @param entity 学员实体
     * @param res 学员端个人信息响应
     */
    private void fillClassAndCourseInfo(EduStudentEntity entity, StudentProfileRes res) {
        if (entity.getClassId() == null) {
            return;
        }

        EduClassEntity classEntity = eduClassMapper.selectById(entity.getClassId());
        if (classEntity == null) {
            return;
        }

        res.setClassTerm(classEntity.getClassTerm());
        if (classEntity.getClassTerm() != null) {
            res.setClassName("第" + classEntity.getClassTerm() + "期");
        }

        res.setCourseId(classEntity.getCourseId());
        if (classEntity.getCourseId() == null) {
            return;
        }

        EduCourseEntity courseEntity = eduCourseMapper.selectById(classEntity.getCourseId());
        if (courseEntity != null) {
            res.setCourseName(courseEntity.getCourseName());
        }
    }
}
