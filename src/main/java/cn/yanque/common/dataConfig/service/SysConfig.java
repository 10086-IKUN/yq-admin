package cn.yanque.common.dataConfig.service;

public class SysConfig {

    public static SystemConfigItem<String> jwtSecret = new SystemConfigItem("jwtSecret", "yanque", String.class);

    public static SystemConfigItem<Long> jwtExpire = new SystemConfigItem("jwtExpire", 1000L, Long.class);

    public static SystemConfigItem<Long> signSecretExpireSeconds = new SystemConfigItem("signSecretExpireSeconds", 1000, Long.class);

}
