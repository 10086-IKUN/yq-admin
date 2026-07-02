package cn.yanque.models.studentFront.service;

import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.pojo.entity.StuPermissionEntity;

import java.util.List;


/**
 * StudentFrontService 服务接口。
 *
 * <p>定义对应模块对外暴露的业务能力，控制层和其他服务通过该接口调用。</p>
 */
public interface StudentFrontService {

    String getPasswordByPhone(String phone);

    Long getStudentIdByPhone(String phone);

    EduStudentEntity getStudentByPhone(String phone);

    EduStudentEntity getStudentById(Long studentId);

    List<StuPermissionEntity> getPermissionsByStudentId(Long studentId);
}
