package com.ls.demo.gmallredis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "RedisController", description = "redis Controller")
@Controller
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation("redis put test")
    @GetMapping("/put")
    @ResponseBody
    public String put(){
        try {
            redisTemplate.opsForValue().set("mall1","test mall redis");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "true";
    }

    @ApiOperation("redis get test")
    @GetMapping("/get")
    @ResponseBody
    public String get(){
        redisTemplate.opsForValue().get("mall1");
        return "false";
    }
}
