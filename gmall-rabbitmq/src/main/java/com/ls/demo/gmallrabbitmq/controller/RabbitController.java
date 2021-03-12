package com.ls.demo.gmallrabbitmq.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.ls.demo.gmallrabbitmq.common.api.CommonResult;
import com.ls.demo.gmallrabbitmq.direct.DirectSender;
import com.ls.demo.gmallrabbitmq.fanout.FanoutSender;
import com.ls.demo.gmallrabbitmq.simple.SimpleReceiver;
import com.ls.demo.gmallrabbitmq.simple.SimpleSender;
import com.ls.demo.gmallrabbitmq.topic.TopicSender;
import com.ls.demo.gmallrabbitmq.work.WorkSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "RabbitController", description = "RabbitMQ功能测试")
@Controller
public class RabbitController {
    @Autowired
    SimpleSender simpleSender;
    @Autowired
    WorkSender workSender;
    @Autowired
    FanoutSender fanoutSender;
    @Autowired
    DirectSender directSender;
    @Autowired
    TopicSender topicSender;

    @ApiOperation("简单模式-生产")
    @RequestMapping(value = "/simple", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult simpleSendTest() {
        for(int i=0;i<10;i++){
            simpleSender.send();
            ThreadUtil.sleep(1000);
        }
        return CommonResult.success(null);
    }

    @ApiOperation("工作模式")
    @RequestMapping(value = "/work", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult workTest() {
        for(int i=0;i<10;i++){
            workSender.send(i);
            ThreadUtil.sleep(1000);
        }
        return CommonResult.success(null);
    }

    /**
     * 多个队列绑定到一个交换机，消息发送到交换机在发送到绑定该交换机的所有队列。
     * @return
     */
    @ApiOperation("发布/订阅模式")
    @RequestMapping(value = "/fanout", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult fanoutTest() {
        for(int i=0;i<10;i++){
            fanoutSender.send(i);
            ThreadUtil.sleep(1000);
        }
        fanoutSender.send(1);
        return CommonResult.success(null);
    }

    /**
     * 多个队列绑定到一个交换机，消息发送到交换机，交换机根据routkey判断发送到那个队列上
     * @return
     */
    @ApiOperation("路由模式")
    @RequestMapping(value = "/direct", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult directTest() {
        for(int i=0;i<10;i++){
            directSender.send(i);
            ThreadUtil.sleep(1000);
        }
        return CommonResult.success(null);
    }

    @ApiOperation("通配符模式")
    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult topicTest() {
        for(int i=0;i<10;i++){
            topicSender.send(i);
            ThreadUtil.sleep(1000);
        }
        return CommonResult.success(null);
    }
}
