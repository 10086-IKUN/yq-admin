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

/**
 * 学生服务接口
 * 定义学生管理的业务逻辑方法
 */
public interface EduStudentService {

    /**
     * 添加学生
     * @param req 创建学生请求参数
     * @return 创建成功的学生信息
     */
    StudentCreateRes addStudent(StudentCreateReq req);

    /**
     * 修改学生
     * @param req 更新学生请求参数
     * @return 更新后的学生信息
     */
    StudentUpdateRes updateStudent(StudentUpdateReq req);

    /**
     * 删除学生
     * @param id 学生ID
     * @return 删除结果
     */
    StudentDeleteRes deleteStudent(Long id);

    /**
     * 根据ID查询学生
     * @param id 学生ID
     * @return 学生详细信息
     */
    StudentDetailRes getStudentById(Long id);

    /**
     * 分页查询学生
     * @param req 分页查询参数
     * @return 分页学生列表
     */
    PageResult<StudentPageRes> pageStudent(StudentPageReq req);
}
