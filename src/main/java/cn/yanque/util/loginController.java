package cn.yanque.util;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class loginController {

    @GetMapping("/login")
    public boolean login(@RequestParam String phone) {
        return SmsUtil.loginByPhoneAndCode(phone);
    }

}
