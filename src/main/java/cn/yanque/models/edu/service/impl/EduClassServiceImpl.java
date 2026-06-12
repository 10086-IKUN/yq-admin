package cn.yanque.models.edu.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.mapper.EduCampusMapper;
import cn.yanque.models.edu.mapper.EduClassMapper;
import cn.yanque.models.edu.mapper.EduCourseMapper;
import cn.yanque.common.pojo.entity.EduCampusEntity;
import cn.yanque.common.pojo.entity.EduClassEntity;
import cn.yanque.common.pojo.entity.EduCourseEntity;
import cn.yanque.common.pojo.vo.req.ClassCreateReq;
import cn.yanque.common.pojo.vo.req.ClassPageReq;
import cn.yanque.common.pojo.vo.req.ClassUpdateReq;
import cn.yanque.common.pojo.vo.res.ClassCreateRes;
import cn.yanque.common.pojo.vo.res.ClassDeleteRes;
import cn.yanque.common.pojo.vo.res.ClassDetailRes;
import cn.yanque.common.pojo.vo.res.ClassPageRes;
import cn.yanque.common.pojo.vo.res.ClassUpdateRes;
import cn.yanque.models.edu.service.EduClassService;
import cn.yanque.models.users.mapper.SysUserMapper;
import cn.yanque.common.pojo.entity.SysUserEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 班级服务实现类
 *
 * 核心优化：批量查询解决N+1查询问题
 * 优化前：10条数据 × 3次查询 = 30次数据库查询
 * 优化后：10条数据只需要3次批量查询
 */
@Service
public class EduClassServiceImpl implements EduClassService {

    @Autowired
    private EduClassMapper eduClassMapper;

    @Autowired
    private EduCampusMapper eduCampusMapper;

    @Autowired
    private EduCourseMapper eduCourseMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 添加班级
     * @param req 创建班级请求参数
     * @return 创建成功的班级ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassCreateRes addClass(ClassCreateReq req) {
        EduClassEntity entity = new EduClassEntity();
        entity.setClassTerm(req.getClassTerm());
        entity.setCampusId(req.getCampusId());
        entity.setHeadTeacherId(req.getHeadTeacherId());
        entity.setClassStatus(req.getClassStatus());
        entity.setStartTime(req.getStartTime());
        entity.setCourseId(req.getCourseId());
        entity.setStudentCount(0);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        eduClassMapper.insert(entity);

        ClassCreateRes res = new ClassCreateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 修改班级
     * @param req 更新班级请求参数
     * @return 更新后的班级ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassUpdateRes updateClass(ClassUpdateReq req) {
        EduClassEntity entity = eduClassMapper.selectById(req.getId());
        if (entity == null) {
            throw BusinessException.ClassNotExist;
        }

        entity.setClassTerm(req.getClassTerm());
        entity.setCampusId(req.getCampusId());
        entity.setHeadTeacherId(req.getHeadTeacherId());
        entity.setClassStatus(req.getClassStatus());
        entity.setStartTime(req.getStartTime());
        entity.setCourseId(req.getCourseId());
        entity.setUpdatedAt(new Date());

        int rows = eduClassMapper.updateById(entity);
        if (rows == 0) {
            throw BusinessException.ClassNotExist;
        }

        ClassUpdateRes res = new ClassUpdateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 删除班级
     * @param id 班级ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassDeleteRes deleteClass(Long id) {
        EduClassEntity entity = eduClassMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.ClassNotExist;
        }

        int rows = eduClassMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.ClassNotExist;
        }

        ClassDeleteRes res = new ClassDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 根据ID查询班级详情
     * @param id 班级ID
     * @return 班级详细信息（含校区、课程、班主任名称）
     */
    @Override
    public ClassDetailRes getClassById(Long id) {
        EduClassEntity entity = eduClassMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.ClassNotExist;
        }
        return buildClassDetailRes(entity);
    }

    /**
     * 分页查询班级（优化版：批量查询关联名称）
     *
     * 优化前（N+1问题）：
     * - 10条数据需要 10×3=30次数据库查询
     *
     * 优化后（批量查询）：
     * - 10条数据只需要 3次数据库查询
     *
     * @param req 分页查询参数（关键词、状态、校区ID）
     * @return 分页班级列表
     */
    @Override
    public PageResult<ClassPageRes> pageClass(ClassPageReq req) {
        // 1. 分页查询班级列表
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<EduClassEntity> list = eduClassMapper.selectPage(
                req.getKeyword(),
                req.getClassStatus(),
                req.getCampusId()
        );
        PageInfo<EduClassEntity> pageInfo = new PageInfo<>(list);

        // 2. 批量查询关联数据（核心优化）
        Map<Long, String> campusMap = batchGetCampusNames(list);
        Map<Long, String> courseMap = batchGetCourseNames(list);
        Map<Long, String> userMap = batchGetUserNames(list);

        // 3. 转换为VO并填充关联名称
        List<ClassPageRes> records = list.stream()
                .map(entity -> buildClassPageRes(entity, campusMap, courseMap, userMap))
                .toList();

        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    /**
     * 批量获取校区名称（优化：1次查询代替N次）
     *
     * 执行流程：
     * 1. 从班级列表中提取所有不重复的campusId
     * 2. 一次性查询所有校区
     * 3. 转换为 Map<校区ID, 校区名称>
     *
     * @param classList 班级列表
     * @return 校区ID -> 校区名称 的映射
     */
    private Map<Long, String> batchGetCampusNames(List<EduClassEntity> classList) {
        // 1. 收集所有不重复的campusId（过滤null）
        Set<Long> campusIds = classList.stream()
                .map(EduClassEntity::getCampusId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // 2. 如果没有需要查询的ID，返回空Map
        if (campusIds.isEmpty()) {
            return Map.of();
        }

        // 3. 批量查询（1次SQL查询）
        List<EduCampusEntity> campuses = eduCampusMapper.selectByIds(campusIds);

        // 4. 转换为 Map<ID, 名称>
        return campuses.stream()
                .collect(Collectors.toMap(EduCampusEntity::getId, EduCampusEntity::getCampusName));
    }

    /**
     * 批量获取课程名称（优化：1次查询代替N次）
     */
    private Map<Long, String> batchGetCourseNames(List<EduClassEntity> classList) {
        Set<Long> courseIds = classList.stream()
                .map(EduClassEntity::getCourseId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (courseIds.isEmpty()) {
            return Map.of();
        }

        List<EduCourseEntity> courses = eduCourseMapper.selectByIds(courseIds);

        return courses.stream()
                .collect(Collectors.toMap(EduCourseEntity::getId, EduCourseEntity::getCourseName));
    }

    /**
     * 批量获取班主任昵称（优化：1次查询代替N次）
     */
    private Map<Long, String> batchGetUserNames(List<EduClassEntity> classList) {
        Set<Long> teacherIds = classList.stream()
                .map(EduClassEntity::getHeadTeacherId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (teacherIds.isEmpty()) {
            return Map.of();
        }

        List<SysUserEntity> users = sysUserMapper.selectByIds(teacherIds);

        return users.stream()
                .collect(Collectors.toMap(SysUserEntity::getId, SysUserEntity::getNickname));
    }

    /**
     * 构建班级详情响应对象（单条查询，使用原方法）
     * @param entity 班级实体
     * @return 包含关联名称的班级详情
     */
    private ClassDetailRes buildClassDetailRes(EduClassEntity entity) {
        ClassDetailRes res = new ClassDetailRes();
        BeanUtils.copyProperties(entity, res);
        fillRelatedNames(entity.getCampusId(), entity.getCourseId(), entity.getHeadTeacherId(),
                res::setCampusName, res::setCourseName, res::setHeadTeacherName);
        return res;
    }

    /**
     * 构建班级分页响应对象（批量查询版）
     *
     * 优化点：直接从Map中获取名称，不再查询数据库
     *
     * @param entity 班级实体
     * @param campusMap 校区ID -> 校区名称
     * @param courseMap 课程ID -> 课程名称
     * @param userMap 用户ID -> 用户昵称
     * @return 包含关联名称的班级分页信息
     */
    private ClassPageRes buildClassPageRes(EduClassEntity entity,
                                           Map<Long, String> campusMap,
                                           Map<Long, String> courseMap,
                                           Map<Long, String> userMap) {
        ClassPageRes res = new ClassPageRes();
        BeanUtils.copyProperties(entity, res);

        // 从Map中获取名称（O(1)时间复杂度）
        if (entity.getCampusId() != null) {
            res.setCampusName(campusMap.get(entity.getCampusId()));
        }
        if (entity.getCourseId() != null) {
            res.setCourseName(courseMap.get(entity.getCourseId()));
        }
        if (entity.getHeadTeacherId() != null) {
            res.setHeadTeacherName(userMap.get(entity.getHeadTeacherId()));
        }

        return res;
    }

    /**
     * 填充关联实体的名称字段（用于单条查询场景）
     *
     * 使用Consumer函数式接口，避免重复代码
     *
     * @param campusId 校区ID
     * @param courseId 课程ID
     * @param headTeacherId 班主任用户ID
     * @param setCampusName 校区名称设置器
     * @param setCourseName 课程名称设置器
     * @param setHeadTeacherName 班主任昵称设置器
     */
    private void fillRelatedNames(Long campusId, Long courseId, Long headTeacherId,
                                  java.util.function.Consumer<String> setCampusName,
                                  java.util.function.Consumer<String> setCourseName,
                                  java.util.function.Consumer<String> setHeadTeacherName) {
        if (campusId != null) {
            EduCampusEntity campus = eduCampusMapper.selectById(campusId);
            if (campus != null) setCampusName.accept(campus.getCampusName());
        }
        if (courseId != null) {
            EduCourseEntity course = eduCourseMapper.selectById(courseId);
            if (course != null) setCourseName.accept(course.getCourseName());
        }
        if (headTeacherId != null) {
            SysUserEntity user = sysUserMapper.selectById(headTeacherId);
            if (user != null) setHeadTeacherName.accept(user.getNickname());
        }
    }
}
