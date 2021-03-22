package com.ls.demo.gmalles.dao;


import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.ls.demo.gmalles.nosql.elasticsearch.document.EsProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 搜索系统中的商品管理自定义Dao
 * Created by macro on 2018/6/19.
 */
public interface EsProductDao extends Mapper<EsProduct> {
    List<EsProduct> getAllEsProductList(@Param("id") Long id);
}
