package cn.yanque.models.studentFront.biz;

import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;

/**
 * 学员端个人中心业务接口
 * 定义学员端个人中心相关的业务逻辑方法
 */
public interface StudentProfileBiz {

    /**
     * 获取个人信息
     * @return 学员信息
     */
    EduStudentEntity getProfile();

    /**
     * 修改个人信息
     * @param entity 学员信息
     */
    void updateProfile(EduStudentEntity entity);
}
