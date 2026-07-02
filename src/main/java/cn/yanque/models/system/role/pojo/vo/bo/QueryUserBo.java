package cn.yanque.models.system.role.pojo.vo.bo;

import lombok.Data;

import java.util.List;

@Data

/**
 * QueryUserBo 业务查询对象。
 *
 * <p>用于在服务层或 Mapper 层之间传递组合查询条件。</p>
 */
public class QueryUserBo {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String realName;
    private String phone;
    private String email;
    private String unionId;
    private String status;
    private List<Long> ids;
}
