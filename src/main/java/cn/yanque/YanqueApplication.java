package cn.yanque;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用程序启动类
 */
@SpringBootApplication
@EnableScheduling
@MapperScans({@MapperScan("cn.yanque.*.*.*.mapper"),@MapperScan("cn.yanque.*.*.mapper")})
public class YanqueApplication {

    public static void main(String[] args) {
        SpringApplication.run(YanqueApplication.class);
    }
}
