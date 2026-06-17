package cn.yanque.models.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /** OSS 外网 Endpoint，例如 oss-cn-beijing.aliyuncs.com。 */
    private String endpoint;

    /** OSS Bucket 名称，例如 yq-homework。 */
    private String bucket;

    /** 阿里云 AccessKeyId，配置中从环境变量读取。 */
    private String accessKeyId;

    /** 阿里云 AccessKeySecret，配置中从环境变量读取。 */
    private String accessKeySecret;

    /** OSS 根目录前缀，默认把作业相关文件放到 homework 目录下。 */
    private String dirPrefix = "homework";

    /** 预览和下载签名地址有效期，单位：分钟。 */
    private Integer previewExpireMinutes = 10;

    /** 单个文件最大大小，单位：MB。 */
    private Long maxFileSizeMb = 50L;
}
