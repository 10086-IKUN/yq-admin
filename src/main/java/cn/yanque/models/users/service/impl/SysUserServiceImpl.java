package cn.yanque.models.users.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.jwt.JWTUtil;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.dataConfig.service.SysConfig;
import cn.yanque.common.dataConfig.service.SysConfigService;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.users.mapper.SysPermissionMapper;
import cn.yanque.models.users.mapper.SysRoleMapper;
import cn.yanque.models.users.mapper.SysUserMapper;
import cn.yanque.common.pojo.entity.SysPermissionEntity;
import cn.yanque.common.pojo.entity.SysRoleEntity;
import cn.yanque.common.pojo.entity.SysUserEntity;
import cn.yanque.common.pojo.info.UserInfo;
import cn.yanque.common.pojo.vo.bo.QueryPermissionBo;
import cn.yanque.common.pojo.vo.bo.QueryUserBo;
import cn.yanque.common.pojo.vo.req.*;
import cn.yanque.common.pojo.vo.res.*;
import cn.yanque.models.users.service.SysUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 系统用户服务实现类
 * 实现用户管理、登录认证、角色分配、用户信息查询等业务逻辑
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();


    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SysConfigService sysConfigService;


    /**
     * 添加用户
     * @param req 创建用户请求参数
     * @return 创建成功的用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCreateRes addUser(UserCreateReq req) {

        SysUserEntity user = new SysUserEntity();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setNickname(req.getNickname());
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setStatus(req.getStatus());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        try {
            sysUserMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw BusinessException.UserExist;
        }

        UserCreateRes res = new UserCreateRes();
        res.setId(user.getId());
        return res;
    }

    /**
     * 修改用户
     * @param req 更新用户请求参数
     * @return 更新后的用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserUpdateRes updateUser(UserUpdateReq req) {
        SysUserEntity user = new SysUserEntity();
        user.setId(req.getId());
        user.setNickname(req.getNickname());
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setUnionId(req.getUnionId());
        user.setStatus(req.getStatus());
        user.setUpdatedAt(new Date());

        int rows;
        try {
            rows = sysUserMapper.updateById(user);
        } catch (DuplicateKeyException e) {
            throw BusinessException.UserExist;
        }
        if (rows == 0) {
            throw BusinessException.UserNotExist;
        }
        UserUpdateRes res = new UserUpdateRes();
        res.setId(req.getId());
        return res;
    }

    /**
     * 删除用户（同时删除用户角色关联）
     * @param id 用户ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDeleteRes deleteUser(Long id) {
        sysUserMapper.deleteUserRoles(id);
        int rows = sysUserMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.UserNotExist;
        }
        UserDeleteRes res = new UserDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 根据ID查询用户详情（含角色ID列表）
     * @param id 用户ID
     * @return 用户详细信息
     */
    @Override
    public UserDetailRes getUserById(Long id) {
        SysUserEntity user = sysUserMapper.selectById(id);
        if (user == null) {
            throw BusinessException.UserNotExist;
        }
        UserDetailRes res = buildUserDetailRes(user);
        List<Long> roleIds = sysUserMapper.selectRoleIdsByUserId(id);
        res.setRoleIds(roleIds);
        return res;
    }

    /**
     * 分页查询用户
     * @param req 分页查询参数（关键词、状态、角色编码）
     * @return 分页用户列表
     */
    @Override
    public PageResult<UserPageRes> pageUser(UserPageReq req) {
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        List<SysUserEntity> list = sysUserMapper.selectPage(req.getKeyword(), req.getStatus(), req.getRoleCode());
        PageInfo<SysUserEntity> pageInfo = new PageInfo<>(list);
        List<UserPageRes> records = list.stream().map(user -> buildUserPageRes(user)).toList();
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, records);
    }

    /**
     * 分配用户角色（先删后插，全量替换）
     * @param userId 用户ID
     * @param req 角色分配请求参数
     * @return 角色分配结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRoleAssignRes assignUserRoles(Long userId, UserRoleAssignReq req) {
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.UserNotExist;
        }

        sysUserMapper.deleteUserRoles(userId);
        if (!req.getRoleIds().isEmpty()) {
            sysUserMapper.insertUserRoles(userId, req.getRoleIds());
        }

        UserRoleAssignRes res = new UserRoleAssignRes();
        res.setUserId(userId);
        res.setRoleIds(req.getRoleIds());
        return res;
    }

    /**
     * 用户登录
     * 验证用户名密码，生成JWT Token和签名密钥，返回用户信息、角色和权限
     * @param req 登录请求参数
     * @return 登录结果（含Token、用户信息、角色、权限）
     */
    @Override
    public LoginRes loginReq(LoginReq req) {
        // 查询用户
        SysUserEntity user = sysUserMapper.selectByUsername(req.getUsername());
        // 用户不存在
        if(user == null){
            throw BusinessException.UserNotExist;
        }
        // 密码错误
        if(!user.getPassword().equals(req.getPassword())){
            throw BusinessException.PasswordError;
        }
        // 生成token
        String token = createToken(user);

        // 生成签名密钥并写入Redis
        String signSecret = createSignSecret();
        String signSecretKey = "yanque:sign:secret:" + user.getId();
        stringRedisTemplate.opsForValue().set(signSecretKey, signSecret, sysConfigService.getConfig(SysConfig.signSecretExpireSeconds), TimeUnit.SECONDS);


        LoginRes res = new LoginRes();
        res.setToken(token);
        res.setSignSecret(signSecret);
        res.setUserDetailRes(buildUserDetailRes(user));

        UserInfo userInfo = getUserInfo(user.getId());
        // set用户角色
        res.setRoleDetailResList(userInfo.getSysRoleEntities().stream().map(role -> {
            RoleDetailRes roleDetailRes = new RoleDetailRes();
            BeanUtils.copyProperties(role, roleDetailRes);
            return roleDetailRes;
        }).toList());
        // set用户权限

        res.setPermissionDetailResList(userInfo.getSysPermissionEntities().stream().map(permission -> {
            PermissionDetailRes permissionDetailRes = new PermissionDetailRes();
            BeanUtils.copyProperties(permission, permissionDetailRes);
            return permissionDetailRes;
        }).toList());
        // 返回结果
        return res;
    }


    /**
     * 获取用户完整信息（含角色列表和权限列表）
     * @param userId 用户ID
     * @return 用户信息（角色+权限）
     */
    @Override
    public UserInfo getUserInfo(Long userId){
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null){
            throw BusinessException.UserNotExist;
        }
        UserInfo userInfo = new UserInfo();

        List<Long> roleIds = sysUserMapper.selectRoleIdsByUserId(user.getId());
        if (roleIds == null || roleIds.isEmpty()){
            return userInfo;
        }
        // 查询用户角色
        QueryUserBo queryUserBo = new QueryUserBo();
        queryUserBo.setIds(roleIds);
        List<SysRoleEntity> sysRoleEntities = sysRoleMapper.selectList(queryUserBo);
        if (sysRoleEntities.isEmpty()){
            return userInfo;
        }
        userInfo.setSysRoleEntities(sysRoleEntities);

        // 查询用户权限
        List<Long> permissionIds = sysRoleMapper.selectPermissionIdsByRoleId(roleIds);
        if (permissionIds.isEmpty()){
            return userInfo;
        }
        QueryPermissionBo queryPermissionBo = new QueryPermissionBo();
        queryPermissionBo.setIds(permissionIds);
        List<SysPermissionEntity> sysPermissionEntities = sysPermissionMapper.selectList(queryPermissionBo);
        userInfo.setSysPermissionEntities(sysPermissionEntities);
        return userInfo;
    }

    /**
     * 创建JWT Token
     * @param sysUserEntity 用户实体
     * @return JWT Token字符串
     */
    private String createToken(SysUserEntity sysUserEntity) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", sysUserEntity.getId());
        long expireSeconds = sysConfigService.getConfig(SysConfig.jwtExpire);
        long expireTimestamp = System.currentTimeMillis() + (expireSeconds * 1000);
        map.put("expire_time", expireTimestamp);
        return JWTUtil.createToken(map, sysConfigService.getConfig(SysConfig.jwtSecret).getBytes());
    }

    /**
     * 构建用户详情响应对象
     * @param user 用户实体
     * @return 用户详情
     */
    private UserDetailRes buildUserDetailRes(SysUserEntity user) {
        UserDetailRes res = new UserDetailRes();
        BeanUtils.copyProperties(user, res);
        return res;
    }

    /**
     * 构建用户分页响应对象
     * @param user 用户实体
     * @return 用户分页信息
     */
    private UserPageRes buildUserPageRes(SysUserEntity user) {
        UserPageRes res = new UserPageRes();
        BeanUtils.copyProperties(user, res);
        return res;
    }

    /**
     * 生成随机签名密钥（32字节Base64URL编码）
     * @return 签名密钥字符串
     */
    private String createSignSecret() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
