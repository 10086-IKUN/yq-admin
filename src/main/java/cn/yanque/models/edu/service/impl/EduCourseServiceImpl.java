package cn.yanque.models.edu.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.mapper.EduCourseMapper;
import cn.yanque.common.pojo.entity.EduCourseEntity;
import cn.yanque.common.pojo.vo.req.CourseCreateReq;
import cn.yanque.common.pojo.vo.req.CoursePageReq;
import cn.yanque.common.pojo.vo.req.CourseUpdateReq;
import cn.yanque.common.pojo.vo.res.CourseCreateRes;
import cn.yanque.common.pojo.vo.res.CourseDeleteRes;
import cn.yanque.common.pojo.vo.res.CourseDetailRes;
import cn.yanque.common.pojo.vo.res.CoursePageRes;
import cn.yanque.common.pojo.vo.res.CourseUpdateRes;
import cn.yanque.models.edu.service.EduCourseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EduCourseServiceImpl implements EduCourseService {

    @Autowired
    private EduCourseMapper eduCourseMapper;

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

    @Override
    public CourseDetailRes getCourseById(Long id) {
        EduCourseEntity entity = eduCourseMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.CourseNotExist;
        }
        return buildCourseDetailRes(entity);
    }

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

    private CourseDetailRes buildCourseDetailRes(EduCourseEntity entity) {
        CourseDetailRes res = new CourseDetailRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private CoursePageRes buildCoursePageRes(EduCourseEntity entity) {
        CoursePageRes res = new CoursePageRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }
}
