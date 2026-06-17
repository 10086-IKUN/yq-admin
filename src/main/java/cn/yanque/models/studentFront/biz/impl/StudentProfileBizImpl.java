package cn.yanque.models.studentFront.biz.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.biz.StudentProfileBiz;
import cn.yanque.models.studentFront.service.StudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 学员端个人中心业务实现类
 * 实现学员端个人中心相关的业务逻辑
 */
@Component
public class StudentProfileBizImpl implements StudentProfileBiz {

    @Autowired
    private StudentProfileService studentProfileService;

    /**
     * 获取个人信息
     * @return 学员信息
     */
    @Override
    public EduStudentEntity getProfile() {
        // TODO: 从JWT中获取学员ID，暂时返回空
        Long studentId = 1L; // 临时硬编码
        EduStudentEntity entity = studentProfileService.getById(studentId);
        if (entity == null) {
            throw new BusinessException(404, "学员不存在");
        }
        return entity;
    }

    /**
     * 修改个人信息
     * @param entity 学员信息
     */
    @Override
    public void updateProfile(EduStudentEntity entity) {
        // TODO: 从JWT中获取学员ID，暂时返回空
        Long studentId = 1L; // 临时硬编码
        entity.setId(studentId);
        studentProfileService.update(entity);
    }
}
