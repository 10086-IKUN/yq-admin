package cn.yanque.models.studentFront.service;

import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.pojo.entity.StuPermissionEntity;

import java.util.List;

public interface StudentFrontService {

    String getPasswordByPhone(String phone);

    Long getStudentIdByPhone(String phone);

    EduStudentEntity getStudentByPhone(String phone);

    EduStudentEntity getStudentById(Long studentId);

    List<StuPermissionEntity> getPermissionsByStudentId(Long studentId);
}
