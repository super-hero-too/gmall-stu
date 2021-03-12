package com.ls.demo.gmallmybatisplus1.controller;

import com.ls.demo.gmallmybatisplus1.api.CommonResult;
import com.ls.demo.gmallmybatisplus1.mbg.model.PmsBrand;
import com.ls.demo.gmallmybatisplus1.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 测试类
 */
@Api(tags = "DemoController", description = "测试接口")
@Controller
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    DemoService demoService;

    /**
     * 查询数据
     *
     * @return
     */
    @GetMapping("/getListAll")
    @ApiOperation("查询所有的品牌")
    @ResponseBody
    public CommonResult getListAll() {
        List<PmsBrand> list = demoService.list();
        if (list.size() == 0) {
            return CommonResult.failed("没有查询到数据");
        }
        return CommonResult.success(list);
    }

}
