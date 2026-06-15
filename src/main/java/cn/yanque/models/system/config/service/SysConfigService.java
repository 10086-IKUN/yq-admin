package cn.yanque.models.system.config.service;

import cn.hutool.core.convert.Convert;
import cn.yanque.models.system.config.pojo.entity.SysConfigEntity;
import cn.yanque.models.system.config.mapper.SysConfigMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 系统配置服务类
 * 提供系统配置的读取和缓存管理
 */
@Component
public class SysConfigService {
    private final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();
    @Autowired
    private SysConfigMapper sysConfigMapper;
    
    /**
     * 获取系统配置值
     * @param systemConfigItem 系统配置项
     * @return 配置值
     */
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
            cache.put(systemConfigItem.getKey(), convert);
            return convert;
        }
        // 缓存中没有，则返回默认值
        cache.put(systemConfigItem.getKey(), systemConfigItem.getDefaultValue());
        return systemConfigItem.getDefaultValue();
    }

    /**
     * 清除指定配置的缓存
     * @param key 配置键
     */
    public void invalidateCache(String key) {
        cache.invalidate(key);
    }
}
