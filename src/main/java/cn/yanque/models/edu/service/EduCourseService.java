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

public interface EduCourseService {

    CourseCreateRes addCourse(CourseCreateReq req);

    CourseUpdateRes updateCourse(CourseUpdateReq req);

    CourseDeleteRes deleteCourse(Long id);

    CourseDetailRes getCourseById(Long id);

    PageResult<CoursePageRes> pageCourse(CoursePageReq req);
}
