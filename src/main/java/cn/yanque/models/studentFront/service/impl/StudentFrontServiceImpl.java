package cn.yanque.models.studentFront.service.impl;

import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.mapper.StuPermissionMapper;
import cn.yanque.models.studentFront.pojo.entity.StuPermissionEntity;
import cn.yanque.models.studentFront.service.StudentFrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentFrontServiceImpl implements StudentFrontService {

    @Autowired
    private EduStudentMapper eduStudentMapper;

    @Autowired
    private StuPermissionMapper stuPermissionMapper;

    @Override
    public String getPasswordByPhone(String phone) {
        EduStudentEntity student = eduStudentMapper.selectByPhone(phone);
        return student != null ? student.getPassword() : null;
    }

    @Override
    public Long getStudentIdByPhone(String phone) {
        EduStudentEntity student = eduStudentMapper.selectByPhone(phone);
        return student != null ? student.getId() : null;
    }

    @Override
    public EduStudentEntity getStudentByPhone(String phone) {
        return eduStudentMapper.selectByPhone(phone);
    }

    @Override
    public List<StuPermissionEntity> getPermissionsByStudentId(Long studentId) {
        return stuPermissionMapper.selectByStudentId(studentId);
    }
}
