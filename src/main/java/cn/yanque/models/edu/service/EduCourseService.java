package cn.yanque.models.edu.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.CourseCreateReq;
import cn.yanque.common.pojo.vo.req.CoursePageReq;
import cn.yanque.common.pojo.vo.req.CourseUpdateReq;
import cn.yanque.common.pojo.vo.res.CourseCreateRes;
import cn.yanque.common.pojo.vo.res.CourseDeleteRes;
import cn.yanque.common.pojo.vo.res.CourseDetailRes;
import cn.yanque.common.pojo.vo.res.CoursePageRes;
import cn.yanque.common.pojo.vo.res.CourseUpdateRes;

/**
 * 课程服务接口
 * 定义课程管理的业务逻辑方法
 */
public interface EduCourseService {

    /**
     * 添加课程
     * @param req 创建课程请求参数
     * @return 创建成功的课程信息
     */
    CourseCreateRes addCourse(CourseCreateReq req);

    /**
     * 修改课程
     * @param req 更新课程请求参数
     * @return 更新后的课程信息
     */
    CourseUpdateRes updateCourse(CourseUpdateReq req);

    /**
     * 删除课程
     * @param id 课程ID
     * @return 删除结果
     */
    CourseDeleteRes deleteCourse(Long id);

    /**
     * 根据ID查询课程
     * @param id 课程ID
     * @return 课程详细信息
     */
    CourseDetailRes getCourseById(Long id);

    /**
     * 分页查询课程
     * @param req 分页查询参数
     * @return 分页课程列表
     */
    PageResult<CoursePageRes> pageCourse(CoursePageReq req);
}
