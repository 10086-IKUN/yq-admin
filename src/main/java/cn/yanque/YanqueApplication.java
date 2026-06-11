package cn.yanque;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScans({@MapperScan("cn.yanque.*.*.mapper")})
public class YanqueApplication {

    public static void main(String[] args) {
        SpringApplication.run(YanqueApplication.class);
    }
}
