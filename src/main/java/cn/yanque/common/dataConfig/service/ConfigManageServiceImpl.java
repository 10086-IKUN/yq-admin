package cn.yanque.common.dataConfig.service;

import cn.yanque.common.dataConfig.entity.SysConfigEntity;
import cn.yanque.common.dataConfig.mapper.SysConfigMapper;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.common.pojo.vo.req.ConfigCreateReq;
import cn.yanque.common.pojo.vo.req.ConfigUpdateReq;
import cn.yanque.common.pojo.vo.res.ConfigCreateRes;
import cn.yanque.common.pojo.vo.res.ConfigDeleteRes;
import cn.yanque.common.pojo.vo.res.ConfigDetailRes;
import cn.yanque.common.pojo.vo.res.ConfigPageRes;
import cn.yanque.common.pojo.vo.res.ConfigUpdateRes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigManageServiceImpl implements ConfigManageService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public ConfigCreateRes addConfig(ConfigCreateReq req) {
        // 检查key是否已存在
        SysConfigEntity existing = sysConfigMapper.selectByKey(req.getK());
        if (existing != null) {
            throw new BusinessException(400, "配置Key已存在");
        }
        SysConfigEntity entity = new SysConfigEntity();
        entity.setK(req.getK());
        entity.setV(req.getV());
        sysConfigMapper.insert(entity);

        ConfigCreateRes res = new ConfigCreateRes();
        res.setId(entity.getId());
        return res;
    }

    @Override
    public ConfigUpdateRes updateConfig(ConfigUpdateReq req) {
        SysConfigEntity entity = sysConfigMapper.selectById(req.getId());
        if (entity == null) {
            throw new BusinessException(404, "配置不存在");
        }
        // 检查新的key是否与其他配置冲突
        if (!entity.getK().equals(req.getK())) {
            SysConfigEntity existing = sysConfigMapper.selectByKey(req.getK());
            if (existing != null) {
                throw new BusinessException(400, "配置Key已存在");
            }
        }
        entity.setK(req.getK());
        entity.setV(req.getV());
        sysConfigMapper.updateById(entity);

        ConfigUpdateRes res = new ConfigUpdateRes();
        res.setId(entity.getId());
        return res;
    }

    @Override
    public ConfigDeleteRes deleteConfig(Long id) {
        SysConfigEntity entity = sysConfigMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "配置不存在");
        }
        sysConfigMapper.deleteById(id);

        ConfigDeleteRes res = new ConfigDeleteRes();
        res.setId(id);
        return res;
    }

    @Override
    public ConfigDetailRes getConfigById(Long id) {
        SysConfigEntity entity = sysConfigMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "配置不存在");
        }
        ConfigDetailRes res = new ConfigDetailRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    @Override
    public List<ConfigPageRes> listAll() {
        List<SysConfigEntity> list = sysConfigMapper.selectAll();
        return list.stream().map(entity -> {
            ConfigPageRes res = new ConfigPageRes();
            BeanUtils.copyProperties(entity, res);
            return res;
        }).collect(Collectors.toList());
    }
}
