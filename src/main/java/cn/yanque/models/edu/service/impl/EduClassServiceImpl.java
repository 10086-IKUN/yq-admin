package cn.yanque.models.edu.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.mapper.EduClassMapper;
import cn.yanque.common.pojo.entity.EduClassEntity;
import cn.yanque.common.pojo.vo.req.ClassCreateReq;
import cn.yanque.common.pojo.vo.req.ClassPageReq;
import cn.yanque.common.pojo.vo.req.ClassUpdateReq;
import cn.yanque.common.pojo.vo.res.ClassCreateRes;
import cn.yanque.common.pojo.vo.res.ClassDeleteRes;
import cn.yanque.common.pojo.vo.res.ClassDetailRes;
import cn.yanque.common.pojo.vo.res.ClassPageRes;
import cn.yanque.common.pojo.vo.res.ClassUpdateRes;
import cn.yanque.models.edu.service.EduClassService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class EduClassServiceImpl implements EduClassService {

    @Autowired
    private EduClassMapper eduClassMapper;

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

    @Override
    public ClassDetailRes getClassById(Long id) {
        EduClassEntity entity = eduClassMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.ClassNotExist;
        }
        return buildClassDetailRes(entity);
    }

    @Override
    public PageResult<ClassPageRes> pageClass(ClassPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<EduClassEntity> list = eduClassMapper.selectPage(
                req.getKeyword(),
                req.getClassStatus(),
                req.getCampusId()
        );
        PageInfo<EduClassEntity> pageInfo = new PageInfo<>(list);

        List<ClassPageRes> records = list.stream().map(this::buildClassPageRes).toList();
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    private ClassDetailRes buildClassDetailRes(EduClassEntity entity) {
        ClassDetailRes res = new ClassDetailRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private ClassPageRes buildClassPageRes(EduClassEntity entity) {
        ClassPageRes res = new ClassPageRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }
}
