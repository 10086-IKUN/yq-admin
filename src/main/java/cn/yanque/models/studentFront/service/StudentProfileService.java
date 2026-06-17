package cn.yanque.models.studentFront.service;

import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;

/**
 * 学员端个人中心服务接口
 * 定义学员端个人中心相关的数据操作方法
 */
public interface StudentProfileService {

    /**
     * 根据ID获取学员信息
     * @param id 学员ID
     * @return 学员信息
     */
    EduStudentEntity getById(Long id);

    /**
     * 修改学员信息
     * @param entity 学员信息
     */
    void update(EduStudentEntity entity);
}
