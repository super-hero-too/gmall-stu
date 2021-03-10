package com.ls.demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 *
 */
@Configuration
@MapperScan({"com.ls.demo.mbg.mapper","com.ls.demo.dao"})
public class MyBatisConfig {
}
