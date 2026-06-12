package cn.yanque.common.dataConfig.service;

/**
 * 系统配置常量类
 * 定义JWT、签名等系统级配置项
 */
public class SysConfig {

    /** JWT签名密钥 */
    public static SystemConfigItem<String> jwtSecret = new SystemConfigItem("jwtSecret", "yanque", String.class);

    /** JWT过期时间（秒） */
    public static SystemConfigItem<Long> jwtExpire = new SystemConfigItem("jwtExpire", 1000L, Long.class);

    /** 签名密钥过期时间（秒） */
    public static SystemConfigItem<Long> signSecretExpireSeconds = new SystemConfigItem("signSecretExpireSeconds", 1000, Long.class);

}
