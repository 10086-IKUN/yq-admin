package cn.yanque.models.system.config.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

/**
 * 系统配置项定义。
 *
 * <p>用于把配置 key、默认值和目标类型绑定在一起，方便 {@link SysConfigService} 做类型化读取。</p>
 */
public class SystemConfigItem<T> {

    /** 数据库配置表中的配置键。 */
    private String key;

    /** 配置不存在或解析失败时使用的默认值。 */
    private T defaultValue;

    /** 目标类型，用于把字符串配置转换成业务代码需要的类型。 */
    private Class<T> clazz;
}
