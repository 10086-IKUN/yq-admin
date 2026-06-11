package cn.yanque.models.edu.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.StudentCreateReq;
import cn.yanque.common.pojo.vo.req.StudentPageReq;
import cn.yanque.common.pojo.vo.req.StudentUpdateReq;
import cn.yanque.common.pojo.vo.res.StudentCreateRes;
import cn.yanque.common.pojo.vo.res.StudentDeleteRes;
import cn.yanque.common.pojo.vo.res.StudentDetailRes;
import cn.yanque.common.pojo.vo.res.StudentPageRes;
import cn.yanque.common.pojo.vo.res.StudentUpdateRes;

public interface EduStudentService {

    StudentCreateRes addStudent(StudentCreateReq req);

    StudentUpdateRes updateStudent(StudentUpdateReq req);

    StudentDeleteRes deleteStudent(Long id);

    StudentDetailRes getStudentById(Long id);

    PageResult<StudentPageRes> pageStudent(StudentPageReq req);
}
