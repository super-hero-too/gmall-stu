package com.ls.demo.gmallmybatisplus1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ls.demo.gmallmybatisplus1.modules.pms.mapper")
public class GmallMybatisPlus1Application {
    public static void main(String[] args) {
        SpringApplication.run(GmallMybatisPlus1Application.class, args);
    }
}
