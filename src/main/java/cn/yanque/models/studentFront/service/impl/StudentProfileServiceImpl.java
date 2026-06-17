package cn.yanque.models.studentFront.service.impl;

import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.service.StudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 学员端个人中心服务实现类
 * 实现学员端个人中心相关的数据操作
 */
@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    @Autowired
    private EduStudentMapper eduStudentMapper;

    /**
     * 根据ID获取学员信息
     * @param id 学员ID
     * @return 学员信息
     */
    @Override
    public EduStudentEntity getById(Long id) {
        return eduStudentMapper.selectById(id);
    }

    /**
     * 修改学员信息
     * @param entity 学员信息
     */
    @Override
    public void update(EduStudentEntity entity) {
        eduStudentMapper.updateById(entity);
    }
}
