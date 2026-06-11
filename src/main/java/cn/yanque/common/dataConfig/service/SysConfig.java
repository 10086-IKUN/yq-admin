package cn.yanque.common.dataConfig.service;

public class SysConfig {

    public static SystemConfigItem<String> jwtSecret = new SystemConfigItem("jwtSecret", "yanque", String.class);

    public static SystemConfigItem<Long> jwtExpire = new SystemConfigItem("jwtExpire", 1000, Long.class);
}
