package cn.yanque.common.dataConfig.entity;

import lombok.Data;

/**
 * 系统配置实体类
 * 对应数据库中的系统配置表
 */
@Data
public class SysConfigEntity {

    /** 配置ID */
    private Long id;

    /** 配置键 */
    private String k;

    /** 配置值 */
    private String v;
}
