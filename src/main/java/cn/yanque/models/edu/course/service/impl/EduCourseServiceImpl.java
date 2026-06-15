package cn.yanque.models.edu.course.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.course.mapper.EduCourseMapper;
import cn.yanque.models.edu.course.pojo.entity.EduCourseEntity;
import cn.yanque.models.edu.course.pojo.vo.req.CourseCreateReq;
import cn.yanque.models.edu.course.pojo.vo.req.CoursePageReq;
import cn.yanque.models.edu.course.pojo.vo.req.CourseUpdateReq;
import cn.yanque.models.edu.course.pojo.vo.res.CourseCreateRes;
import cn.yanque.models.edu.course.pojo.vo.res.CourseDeleteRes;
import cn.yanque.models.edu.course.pojo.vo.res.CourseDetailRes;
import cn.yanque.models.edu.course.pojo.vo.res.CoursePageRes;
import cn.yanque.models.edu.course.pojo.vo.res.CourseUpdateRes;
import cn.yanque.models.edu.course.service.EduCourseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程服务实现类
 * 实现课程管理的业务逻辑，包括增删改查
 */
@Service
public class EduCourseServiceImpl implements EduCourseService {

    @Autowired
    private EduCourseMapper eduCourseMapper;

    /**
     * 添加课程
     * @param req 创建课程请求参数
     * @return 创建成功的课程ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseCreateRes addCourse(CourseCreateReq req) {
        EduCourseEntity entity = new EduCourseEntity();
        entity.setCourseName(req.getCourseName());
        entity.setCourseDays(req.getCourseDays());
        entity.setFeishuMaterialPath(req.getFeishuMaterialPath());
        entity.setStatus(req.getStatus());
        eduCourseMapper.insert(entity);

        CourseCreateRes res = new CourseCreateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 修改课程
     * @param req 更新课程请求参数
     * @return 更新后的课程ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseUpdateRes updateCourse(CourseUpdateReq req) {
        EduCourseEntity entity = eduCourseMapper.selectById(req.getId());
        if (entity == null) {
            throw BusinessException.CourseNotExist;
        }

        entity.setCourseName(req.getCourseName());
        entity.setCourseDays(req.getCourseDays());
        entity.setFeishuMaterialPath(req.getFeishuMaterialPath());
        entity.setStatus(req.getStatus());

        int rows = eduCourseMapper.updateById(entity);
        if (rows == 0) {
            throw BusinessException.CourseNotExist;
        }

        CourseUpdateRes res = new CourseUpdateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 删除课程
     * @param id 课程ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseDeleteRes deleteCourse(Long id) {
        EduCourseEntity entity = eduCourseMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.CourseNotExist;
        }

        int rows = eduCourseMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.CourseNotExist;
        }

        CourseDeleteRes res = new CourseDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 根据ID查询课程详情
     * @param id 课程ID
     * @return 课程详细信息
     */
    @Override
    public CourseDetailRes getCourseById(Long id) {
        EduCourseEntity entity = eduCourseMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.CourseNotExist;
        }
        return buildCourseDetailRes(entity);
    }

    /**
     * 分页查询课程
     * @param req 分页查询参数（关键词、状态）
     * @return 分页课程列表
     */
    @Override
    public PageResult<CoursePageRes> pageCourse(CoursePageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<EduCourseEntity> list = eduCourseMapper.selectPage(
                req.getKeyword(),
                req.getStatus()
        );
        PageInfo<EduCourseEntity> pageInfo = new PageInfo<>(list);

        List<CoursePageRes> records = list.stream().map(this::buildCoursePageRes).toList();
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    /**
     * 构建课程详情响应对象
     * @param entity 课程实体
     * @return 课程详情
     */
    private CourseDetailRes buildCourseDetailRes(EduCourseEntity entity) {
        CourseDetailRes res = new CourseDetailRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    /**
     * 构建课程分页响应对象
     * @param entity 课程实体
     * @return 课程分页信息
     */
    private CoursePageRes buildCoursePageRes(EduCourseEntity entity) {
        CoursePageRes res = new CoursePageRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }
}
