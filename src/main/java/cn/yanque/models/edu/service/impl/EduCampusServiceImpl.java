package cn.yanque.models.edu.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.common.pojo.entity.EduCampusEntity;
import cn.yanque.common.pojo.vo.req.CampusCreateReq;
import cn.yanque.common.pojo.vo.req.CampusPageReq;
import cn.yanque.common.pojo.vo.req.CampusUpdateReq;
import cn.yanque.common.pojo.vo.res.CampusCreateRes;
import cn.yanque.common.pojo.vo.res.CampusDeleteRes;
import cn.yanque.common.pojo.vo.res.CampusDetailRes;
import cn.yanque.common.pojo.vo.res.CampusPageRes;
import cn.yanque.common.pojo.vo.res.CampusUpdateRes;
import cn.yanque.models.edu.mapper.EduCampusMapper;
import cn.yanque.models.edu.service.EduCampusService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class EduCampusServiceImpl implements EduCampusService {

    @Autowired
    private EduCampusMapper eduCampusMapper;

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

    @Override
    public CampusDetailRes getCampusById(Long id) {
        EduCampusEntity entity = eduCampusMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.CampusNotExist;
        }
        return buildCampusDetailRes(entity);
    }

    @Override
    public PageResult<CampusPageRes> pageCampus(CampusPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<EduCampusEntity> list = eduCampusMapper.selectPage(
                req.getKeyword(),
                req.getStatus()
        );
        PageInfo<EduCampusEntity> pageInfo = new PageInfo<>(list);

        List<CampusPageRes> records = list.stream().map(this::buildCampusPageRes).toList();
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    private CampusDetailRes buildCampusDetailRes(EduCampusEntity entity) {
        CampusDetailRes res = new CampusDetailRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private CampusPageRes buildCampusPageRes(EduCampusEntity entity) {
        CampusPageRes res = new CampusPageRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }
}
