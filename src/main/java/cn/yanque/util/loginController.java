package cn.yanque.util;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

/**
 * 短信登录测试入口。
 *
 * <p>当前只暴露 /login，用手机号触发短信工具类里的验证码发送逻辑。</p>
 */
public class loginController {

    @GetMapping("/login")
    public boolean login(@RequestParam String phone) {
        return SmsUtil.loginByPhoneAndCode(phone);
    }

}
