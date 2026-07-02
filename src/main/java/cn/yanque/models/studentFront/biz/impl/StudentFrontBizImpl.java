package cn.yanque.models.studentFront.biz.impl;

import cn.hutool.jwt.JWTUtil;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.studentFront.biz.StudentFrontBiz;
import cn.yanque.models.studentFront.pojo.vo.req.StudentLoginReq;
import cn.yanque.models.studentFront.pojo.vo.res.StudentLoginRes;
import cn.yanque.models.studentFront.service.StudentFrontService;
import cn.yanque.models.system.config.service.SysConfig;
import cn.yanque.models.system.config.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component

/**
 * 学生端登录业务。
 *
 * <p>登录成功后同时签发 JWT 和请求签名密钥。JWT 用于识别学生身份，签名密钥存入 Redis，
 * 供后续接口做 HMAC 防重放校验。</p>
 */
public class StudentFrontBizImpl implements StudentFrontBiz {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String STUDENT_SIGN_SECRET_PREFIX = "yanque:student:sign:secret:";

    @Autowired
    private StudentFrontService studentFrontService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public StudentLoginRes login(StudentLoginReq req) {
        // 学生端先按手机号查询学员，再校验登录密码。
        EduStudentEntity student = studentFrontService.getStudentByPhone(req.getPhone());
        if (student == null || !req.getPassword().equals(student.getPassword())) {
            throw new BusinessException(400, "手机号或密码错误");
        }

        // 登录成功后返回 JWT 和签名密钥，前端后续请求会同时携带二者。
        String token = createToken(student);
        String signSecret = createSignSecret();
        stringRedisTemplate.opsForValue().set(
                STUDENT_SIGN_SECRET_PREFIX + student.getId(),
                signSecret,
                sysConfigService.getConfig(SysConfig.signSecretExpireSeconds),
                TimeUnit.SECONDS);

        StudentLoginRes res = new StudentLoginRes();
        res.setToken(token);
        res.setSignSecret(signSecret);
        res.setStudentInfo(student);
        // 权限列表来自学生当前有效的权限模板。
        res.setPermissions(studentFrontService.getPermissionsByStudentId(student.getId()));
        return res;
    }

    private String createToken(EduStudentEntity student) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", student.getId());
        // 标记 token 类型，让通用 JWT 拦截器区分管理端用户和学生。
        payload.put("user_type", "STUDENT");
        long expireSeconds = sysConfigService.getConfig(SysConfig.jwtExpire);
        payload.put("expire_time", System.currentTimeMillis() + (expireSeconds * 1000));
        return JWTUtil.createToken(payload, sysConfigService.getConfig(SysConfig.jwtSecret).getBytes());
    }

    private String createSignSecret() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
