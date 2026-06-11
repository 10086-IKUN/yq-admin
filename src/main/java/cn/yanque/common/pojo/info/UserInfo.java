package cn.yanque.common.pojo.info;

import cn.yanque.common.pojo.entity.SysPermissionEntity;
import cn.yanque.common.pojo.entity.SysRoleEntity;
import cn.yanque.common.pojo.entity.SysUserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private SysUserEntity sysUserEntity;

    private List<SysPermissionEntity> sysPermissionEntities = new ArrayList<>();

    private List<SysRoleEntity> sysRoleEntities = new ArrayList<>();
}
