package cn.yanque.models.system.config.service.impl;

import cn.yanque.models.system.config.pojo.entity.SysConfigEntity;
import cn.yanque.models.system.config.mapper.SysConfigMapper;
import cn.yanque.models.system.config.service.ConfigManageService;
import cn.yanque.models.system.config.service.SysConfigService;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.system.config.pojo.vo.req.ConfigCreateReq;
import cn.yanque.models.system.config.pojo.vo.req.ConfigUpdateReq;
import cn.yanque.models.system.config.pojo.vo.res.ConfigCreateRes;
import cn.yanque.models.system.config.pojo.vo.res.ConfigDeleteRes;
import cn.yanque.models.system.config.pojo.vo.res.ConfigDetailRes;
import cn.yanque.models.system.config.pojo.vo.res.ConfigPageRes;
import cn.yanque.models.system.config.pojo.vo.res.ConfigUpdateRes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

/**
 * 系统配置管理服务。
 *
 * <p>管理端修改配置后会主动清理 {@link SysConfigService} 的缓存，保证运行时读取到最新配置。</p>
 */
public class ConfigManageServiceImpl implements ConfigManageService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private SysConfigService sysConfigService;

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
        String oldKey = entity.getK();
        
        if (!entity.getK().equals(req.getK())) {
            SysConfigEntity existing = sysConfigMapper.selectByKey(req.getK());
            if (existing != null) {
                throw new BusinessException(400, "配置Key已存在");
            }
        }
        entity.setK(req.getK());
        entity.setV(req.getV());
        sysConfigMapper.updateById(entity);

        sysConfigService.invalidateCache(oldKey);
        if (!oldKey.equals(req.getK())) {
            sysConfigService.invalidateCache(req.getK());
        }

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
        String configKey = entity.getK();
        sysConfigMapper.deleteById(id);

        sysConfigService.invalidateCache(configKey);

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
