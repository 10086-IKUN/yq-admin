package cn.yanque.models.system.config.service;

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

    /** Text-to-SQL 评测页面和断言引擎元数据，数据库未配置时由评测服务使用内置默认值。 */
    public static SystemConfigItem<String> textToSqlEvalMetadata =
            new SystemConfigItem<>("text.to.sql.eval.metadata", "{}", String.class);

    /** 火山引擎日志服务中用于检索 Java 应用日志的默认 Topic ID。 */
    public static SystemConfigItem<String> volcengineTlsDefaultTopicId =
            new SystemConfigItem<>("volcengine.tls.default.topic.id", "", String.class);

}
