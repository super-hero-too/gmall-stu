package com.ls.demo.gmallaop.modules.pms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.demo.gmallaop.api.CommonPage;
import com.ls.demo.gmallaop.api.CommonResult;
import com.ls.demo.gmallaop.modules.pms.model.PmsBrand;
import com.ls.demo.gmallaop.modules.pms.service.PmsBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author ls
 * @since 2021-03-12
 */
@Api(tags = "PmsBrandController", description = "商品品牌")
@RestController
@RequestMapping("/pms/pmsBrand")
public class PmsBrandController {
    @Autowired
    PmsBrandService pmsBrandService;

    @ApiOperation("查询所有商品的品牌无分页")
    @ResponseBody
    @GetMapping("/listAll")
    public CommonResult listAll(){
        List<PmsBrand> list = pmsBrandService.list();
        if(list!=null && list.size()!=0){
            return CommonResult.success(list);
        }
        return CommonResult.failed();
    }

    @ApiOperation("查询商品品牌分页")
    @ResponseBody
    @GetMapping("/listPage")
    public CommonResult getListPage(@RequestParam(value = "pageNum",defaultValue = "1") @ApiParam("开始页码")  Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "2")  @ApiParam("页面大小") Integer pageSize){
        Page<PmsBrand> page = new Page<>(pageNum, pageSize);
        Page<PmsBrand> pageResult = pmsBrandService.page(page);
        return CommonResult.success(CommonPage.restPage(pageResult));
    }
}

