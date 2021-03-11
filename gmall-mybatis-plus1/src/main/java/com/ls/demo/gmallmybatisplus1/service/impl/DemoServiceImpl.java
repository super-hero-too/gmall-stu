package com.ls.demo.gmallmybatisplus1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.demo.gmallmybatisplus1.mbg.mapper.DemoMapper;
import com.ls.demo.gmallmybatisplus1.mbg.model.PmsBrand;
import com.ls.demo.gmallmybatisplus1.service.DemoService;
import org.springframework.stereotype.Service;


@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper,PmsBrand> implements DemoService {

}
