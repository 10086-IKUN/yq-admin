package cn.yanque.common.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 校区实体类
 * 对应数据库中的校区表
 */
@Data
public class EduCampusEntity {

    /** 校区ID */
    private Long id;
    /** 校区名称 */
    private String campusName;
    /** 负责人用户ID */
    private Long principalUserId;
    /** 校区地址 */
    private String address;
    /** 联系电话 */
    private String contactPhone;
    /** 备注 */
    private String remark;
    /** 状态，1启用，0禁用 */
    private Integer status;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}
