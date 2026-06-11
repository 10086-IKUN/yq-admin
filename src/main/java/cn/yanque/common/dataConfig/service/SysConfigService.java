package cn.yanque.common.dataConfig.service;

import cn.hutool.core.convert.Convert;
import cn.yanque.common.dataConfig.entity.SysConfigEntity;
import cn.yanque.common.dataConfig.mapper.SysConfigMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SysConfigService {
    private final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();
    @Autowired
    private SysConfigMapper sysConfigMapper;
    public <T> T getConfig(SystemConfigItem<T> systemConfigItem) {
        // 从缓存中获取
        Object value = cache.getIfPresent(systemConfigItem.getKey());
        if (value != null) {
            return Convert.convert(systemConfigItem.getClazz(), value);
        }
        // 从数据库中获取
        SysConfigEntity sysConfigEntity = sysConfigMapper.selectByKey(systemConfigItem.getKey());
        if (sysConfigEntity != null && sysConfigEntity.getV() != null){
            T convert = Convert.convert(systemConfigItem.getClazz(), sysConfigEntity.getV());
            // 放入缓存
            cache.put(systemConfigItem.getKey(), convert);
            return convert;
        }

        // 返回默认值
        cache.put(systemConfigItem.getKey(), systemConfigItem.getDefaultValue());
        return systemConfigItem.getDefaultValue();
    }
}
