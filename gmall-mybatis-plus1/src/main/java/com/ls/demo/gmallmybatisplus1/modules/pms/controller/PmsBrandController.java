package com.ls.demo.gmallmybatisplus1.modules.pms.controller;


import com.ls.demo.gmallmybatisplus1.api.CommonResult;
import com.ls.demo.gmallmybatisplus1.modules.pms.model.PmsBrand;
import com.ls.demo.gmallmybatisplus1.modules.pms.service.PmsBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author ls
 * @since 2021-03-11
 */
@Api(tags = "PmsBrandController", description = "品牌表")
@RestController
@RequestMapping("/pms/pmsBrand")
public class PmsBrandController {

    @Autowired
    PmsBrandService pmsBrandService;

    @ApiOperation("查询所有的品牌")
    @GetMapping("/getListAll")
    @ResponseBody
    public CommonResult getListAll() {
        List<PmsBrand> list = pmsBrandService.list();
        if (list.size() == 0) {
            return CommonResult.failed("没有查询到数据.");
        }
        return CommonResult.success(list);
    }

}

