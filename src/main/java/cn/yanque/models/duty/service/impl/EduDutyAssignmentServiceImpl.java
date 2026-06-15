package cn.yanque.models.duty.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.clazz.mapper.EduClassMapper;
import cn.yanque.models.edu.clazz.pojo.entity.EduClassEntity;
import cn.yanque.models.duty.pojo.entity.EduDutyAssignmentEntity;
import cn.yanque.models.system.user.pojo.entity.SysUserEntity;
import cn.yanque.models.duty.pojo.vo.req.DutyAssignmentCreateReq;
import cn.yanque.models.duty.pojo.vo.req.DutyAssignmentPageReq;
import cn.yanque.models.duty.pojo.vo.req.DutyAssignmentUpdateReq;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentCreateRes;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentDeleteRes;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentDetailRes;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentPageRes;
import cn.yanque.models.duty.pojo.vo.res.DutyAssignmentUpdateRes;
import cn.yanque.models.duty.mapper.EduDutyAssignmentMapper;
import cn.yanque.models.duty.service.EduDutyAssignmentService;
import cn.yanque.models.system.user.mapper.SysUserMapper;
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
 * 值班安排Service实现类
 */
@Service
public class EduDutyAssignmentServiceImpl implements EduDutyAssignmentService {

    @Autowired
    private EduDutyAssignmentMapper eduDutyAssignmentMapper;

    @Autowired
    private EduClassMapper eduClassMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 新增值班安排
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DutyAssignmentCreateRes addDutyAssignment(DutyAssignmentCreateReq req) {
        // 校验同一日期、同一类型、同一班级不能重复
        int count = eduDutyAssignmentMapper.countByDateAndTypeAndClass(
                req.getDutyDate(), req.getDutyType(), req.getClassId(), null);
        if (count > 0) {
            throw BusinessException.DataError.newInstance("该日期已有相同的值班安排");
        }

        EduDutyAssignmentEntity entity = new EduDutyAssignmentEntity();
        BeanUtils.copyProperties(req, entity);
        eduDutyAssignmentMapper.insert(entity);

        DutyAssignmentCreateRes res = new DutyAssignmentCreateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 更新值班安排
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DutyAssignmentUpdateRes updateDutyAssignment(DutyAssignmentUpdateReq req) {
        EduDutyAssignmentEntity entity = eduDutyAssignmentMapper.selectById(req.getId());
        if (entity == null) {
            throw BusinessException.DataError.newInstance("值班安排不存在");
        }

        // 校验同一日期、同一类型、同一班级不能重复（排除自身）
        int count = eduDutyAssignmentMapper.countByDateAndTypeAndClass(
                req.getDutyDate(), req.getDutyType(), req.getClassId(), req.getId());
        if (count > 0) {
            throw BusinessException.DataError.newInstance("该日期已有相同的值班安排");
        }

        BeanUtils.copyProperties(req, entity);
        eduDutyAssignmentMapper.updateById(entity);

        DutyAssignmentUpdateRes res = new DutyAssignmentUpdateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 删除值班安排
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DutyAssignmentDeleteRes deleteDutyAssignment(Long id) {
        EduDutyAssignmentEntity entity = eduDutyAssignmentMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.DataError.newInstance("值班安排不存在");
        }

        int rows = eduDutyAssignmentMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.DataError.newInstance("值班安排不存在");
        }

        DutyAssignmentDeleteRes res = new DutyAssignmentDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 查询值班安排详情
     */
    @Override
    public DutyAssignmentDetailRes getDutyAssignmentById(Long id) {
        EduDutyAssignmentEntity entity = eduDutyAssignmentMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.DataError.newInstance("值班安排不存在");
        }
        return buildDetailRes(entity);
    }

    /**
     * 分页查询值班安排
     */
    @Override
    public PageResult<DutyAssignmentPageRes> pageDutyAssignment(DutyAssignmentPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<EduDutyAssignmentEntity> list = eduDutyAssignmentMapper.selectPage(
                req.getClassId(),
                req.getDutyType(),
                req.getStartDate(),
                req.getEndDate()
        );
        PageInfo<EduDutyAssignmentEntity> pageInfo = new PageInfo<>(list);

        // 批量查询关联名称
        Map<Long, String> classMap = batchGetClassNames(list);
        Map<Long, String> userMap = batchGetUserNames(list);

        List<DutyAssignmentPageRes> records = list.stream()
                .map(entity -> buildPageRes(entity, classMap, userMap))
                .toList();

        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    /**
     * 批量获取班级名称
     */
    private Map<Long, String> batchGetClassNames(List<EduDutyAssignmentEntity> list) {
        Set<Long> classIds = list.stream()
                .map(EduDutyAssignmentEntity::getClassId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        if (classIds.isEmpty()) {
            return Map.of();
        }

        List<EduClassEntity> classes = eduClassMapper.selectByIds(classIds);
        return classes.stream()
                .collect(Collectors.toMap(EduClassEntity::getId, c -> c.getClassTerm() + "班"));
    }

    /**
     * 批量获取用户昵称
     */
    private Map<Long, String> batchGetUserNames(List<EduDutyAssignmentEntity> list) {
        Set<Long> userIds = list.stream()
                .map(EduDutyAssignmentEntity::getTeacherId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (userIds.isEmpty()) {
            return Map.of();
        }

        List<SysUserEntity> users = sysUserMapper.selectByIds(userIds);
        return users.stream()
                .collect(Collectors.toMap(SysUserEntity::getId, SysUserEntity::getNickname));
    }

    private DutyAssignmentDetailRes buildDetailRes(EduDutyAssignmentEntity entity) {
        DutyAssignmentDetailRes res = new DutyAssignmentDetailRes();
        BeanUtils.copyProperties(entity, res);

        // 填充班级名称
        if (entity.getClassId() != null && entity.getClassId() > 0) {
            EduClassEntity clazz = eduClassMapper.selectById(entity.getClassId());
            if (clazz != null) {
                res.setClassName(clazz.getClassTerm() + "班");
            }
        } else {
            res.setClassName("全局");
        }

        // 填充老师姓名
        if (entity.getTeacherId() != null) {
            SysUserEntity user = sysUserMapper.selectById(entity.getTeacherId());
            if (user != null) {
                res.setTeacherName(user.getNickname());
            }
        }

        return res;
    }

    private DutyAssignmentPageRes buildPageRes(EduDutyAssignmentEntity entity,
                                                Map<Long, String> classMap,
                                                Map<Long, String> userMap) {
        DutyAssignmentPageRes res = new DutyAssignmentPageRes();
        BeanUtils.copyProperties(entity, res);

        // 填充班级名称
        if (entity.getClassId() != null && entity.getClassId() > 0) {
            res.setClassName(classMap.get(entity.getClassId()));
        } else {
            res.setClassName("全局");
        }

        // 填充老师姓名
        if (entity.getTeacherId() != null) {
            res.setTeacherName(userMap.get(entity.getTeacherId()));
        }

        return res;
    }
}
