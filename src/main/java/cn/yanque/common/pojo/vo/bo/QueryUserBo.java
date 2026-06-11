package cn.yanque.common.pojo.vo.bo;

import lombok.Data;

import java.util.List;

@Data
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
