package cn.yanque.models.edu.campus.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.system.user.pojo.entity.SysUserEntity;
import cn.yanque.models.edu.campus.pojo.entity.EduCampusEntity;
import cn.yanque.models.edu.campus.pojo.vo.req.CampusCreateReq;
import cn.yanque.models.edu.campus.pojo.vo.req.CampusPageReq;
import cn.yanque.models.edu.campus.pojo.vo.req.CampusUpdateReq;
import cn.yanque.models.edu.campus.pojo.vo.res.CampusCreateRes;
import cn.yanque.models.edu.campus.pojo.vo.res.CampusDeleteRes;
import cn.yanque.models.edu.campus.pojo.vo.res.CampusDetailRes;
import cn.yanque.models.edu.campus.pojo.vo.res.CampusPageRes;
import cn.yanque.models.edu.campus.pojo.vo.res.CampusUpdateRes;
import cn.yanque.models.edu.campus.mapper.EduCampusMapper;
import cn.yanque.models.edu.campus.service.EduCampusService;
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
 * 校区服务实现类
 * 实现校区管理的业务逻辑
 */
@Service
public class EduCampusServiceImpl implements EduCampusService {

    @Autowired
    private EduCampusMapper eduCampusMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 添加校区
     * @param req 创建校区请求参数
     * @return 创建成功的校区信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CampusCreateRes addCampus(CampusCreateReq req) {
        EduCampusEntity entity = new EduCampusEntity();
        entity.setCampusName(req.getCampusName());
        entity.setPrincipalUserId(req.getPrincipalUserId());
        entity.setAddress(req.getAddress());
        entity.setContactPhone(req.getContactPhone());
        entity.setRemark(req.getRemark());
        entity.setStatus(req.getStatus());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        eduCampusMapper.insert(entity);

        CampusCreateRes res = new CampusCreateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 修改校区
     * @param req 更新校区请求参数
     * @return 更新后的校区信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CampusUpdateRes updateCampus(CampusUpdateReq req) {
        EduCampusEntity entity = eduCampusMapper.selectById(req.getId());
        if (entity == null) {
            throw BusinessException.CampusNotExist;
        }

        entity.setCampusName(req.getCampusName());
        entity.setPrincipalUserId(req.getPrincipalUserId());
        entity.setAddress(req.getAddress());
        entity.setContactPhone(req.getContactPhone());
        entity.setRemark(req.getRemark());
        entity.setStatus(req.getStatus());
        entity.setUpdatedAt(new Date());

        int rows = eduCampusMapper.updateById(entity);
        if (rows == 0) {
            throw BusinessException.CampusNotExist;
        }

        CampusUpdateRes res = new CampusUpdateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 删除校区
     * @param id 校区ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CampusDeleteRes deleteCampus(Long id) {
        EduCampusEntity entity = eduCampusMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.CampusNotExist;
        }

        int rows = eduCampusMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.CampusNotExist;
        }

        CampusDeleteRes res = new CampusDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 根据ID查询校区
     * @param id 校区ID
     * @return 校区详细信息
     */
    @Override
    public CampusDetailRes getCampusById(Long id) {
        EduCampusEntity entity = eduCampusMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.CampusNotExist;
        }
        return buildCampusDetailRes(entity);
    }

    /**
     * 分页查询校区（批量查询优化）
     * @param req 分页查询参数
     * @return 分页校区列表
     */
    @Override
    public PageResult<CampusPageRes> pageCampus(CampusPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<EduCampusEntity> list = eduCampusMapper.selectPage(
                req.getKeyword(),
                req.getStatus()
        );
        PageInfo<EduCampusEntity> pageInfo = new PageInfo<>(list);

        // 批量查询负责人姓名（1次查询代替N次）
        Map<Long, String> userMap = batchGetUserNames(list);

        List<CampusPageRes> records = list.stream()
                .map(entity -> buildCampusPageRes(entity, userMap))
                .toList();
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    /**
     * 批量获取用户昵称（解决N+1查询问题）
     * @param campusList 校区列表
     * @return 用户ID -> 昵称 的映射
     */
    private Map<Long, String> batchGetUserNames(List<EduCampusEntity> campusList) {
        Set<Long> userIds = campusList.stream()
                .map(EduCampusEntity::getPrincipalUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (userIds.isEmpty()) {
            return Map.of();
        }

        List<SysUserEntity> users = sysUserMapper.selectByIds(userIds);
        return users.stream()
                .collect(Collectors.toMap(SysUserEntity::getId, SysUserEntity::getNickname));
    }

    private CampusDetailRes buildCampusDetailRes(EduCampusEntity entity) {
        CampusDetailRes res = new CampusDetailRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private CampusPageRes buildCampusPageRes(EduCampusEntity entity, Map<Long, String> userMap) {
        CampusPageRes res = new CampusPageRes();
        BeanUtils.copyProperties(entity, res);
        // 从Map中获取负责人姓名
        if (entity.getPrincipalUserId() != null) {
            res.setPrincipalUserName(userMap.get(entity.getPrincipalUserId()));
        }
        return res;
    }
}
