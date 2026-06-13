package cn.yanque.models.edu.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.CourseDetailCreateReq;
import cn.yanque.common.pojo.vo.req.CourseDetailPageReq;
import cn.yanque.common.pojo.vo.req.CourseDetailUpdateReq;
import cn.yanque.common.pojo.vo.res.CourseDetailCreateRes;
import cn.yanque.common.pojo.vo.res.CourseDetailDeleteRes;
import cn.yanque.common.pojo.vo.res.CourseDetailDetailRes;
import cn.yanque.common.pojo.vo.res.CourseDetailPageRes;
import cn.yanque.common.pojo.vo.res.CourseDetailUpdateRes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 课程详情Service接口
 *
 * 设计思路：
 * 1. Service层定义业务逻辑方法，Controller调用Service，Service调用Mapper
 * 2. 这种分层架构的好处：
 *    - Controller只负责接收请求和返回响应
 *    - Service负责业务逻辑（参数校验、数据转换、事务管理）
 *    - Mapper只负责数据库操作
 * 3. 接口和实现分离，方便单元测试和后期扩展
 */
public interface EduCourseDetailService {

    /**
     * 新增课程详情
     * @param req 包含课程ID、阶段名称、天数、课程内容
     * @return 返回新创建记录的ID
     */
    CourseDetailCreateRes addCourseDetail(CourseDetailCreateReq req);

    /**
     * 更新课程详情
     * @param req 包含要更新的ID和新的字段值
     * @return 返回被更新记录的ID
     */
    CourseDetailUpdateRes updateCourseDetail(CourseDetailUpdateReq req);

    /**
     * 删除课程详情
     * @param id 要删除的记录ID
     * @return 返回被删除记录的ID
     */
    CourseDetailDeleteRes deleteCourseDetail(Long id);

    /**
     * 根据ID查询课程详情
     * @param id 记录ID
     * @return 返回完整的课程详情信息
     */
    CourseDetailDetailRes getCourseDetailById(Long id);

    /**
     * 分页查询课程详情
     * @param req 包含课程ID和分页参数
     * @return 返回分页结果，包含总条数和当前页数据
     */
    PageResult<CourseDetailPageRes> pageCourseDetail(CourseDetailPageReq req);

    /**
     * 导入Excel数据
     * @param file Excel文件
     */
    void importExcel(Long courseId,MultipartFile file);

    /**
     * 获取课程的所有阶段名称
     * @param courseId 课程ID
     * @return 阶段名称列表
     */
    List<String> getStageNames(Long courseId);
}
