package com.ls.demo.gmalles.service.impl;


import com.ls.demo.gmalles.dao.EsProductDao;
import com.ls.demo.gmalles.nosql.elasticsearch.document.EsProduct;
import com.ls.demo.gmalles.nosql.elasticsearch.repository.EsProductRepository;
import com.ls.demo.gmalles.service.EsProductService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 商品搜索管理Service实现类
 *
 */
@Service
public class EsProductServiceImpl implements EsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsProductServiceImpl.class);
    @Autowired
    private EsProductDao esProductDao;
    @Autowired
    private EsProductRepository esProductRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public int importAll() {
        List<EsProduct> esProductList = esProductDao.getAllEsProductList(null);
        Iterable<EsProduct> esProductIterable = esProductRepository.saveAll(esProductList);
        Iterator<EsProduct> iterator = esProductIterable.iterator();
        int result = 0;
        while (iterator.hasNext()) {
            result++;
            iterator.next();
        }
        return result;
    }

    @Override
    public void delete(Long id) {
        esProductRepository.deleteById(id);
    }

    @Override
    public EsProduct create(Long id) {
        EsProduct result = null;
        List<EsProduct> esProductList = esProductDao.getAllEsProductList(id);
        if (esProductList.size() > 0) {
            EsProduct esProduct = esProductList.get(0);
            result = esProductRepository.save(esProduct);
        }
        return result;
    }

    @Override
    public void delete(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<EsProduct> esProductList = new ArrayList<>();
            for (Long id : ids) {
                EsProduct esProduct = new EsProduct();
                esProduct.setId(id);
                esProductList.add(esProduct);
            }
            esProductRepository.deleteAll(esProductList);
        }
    }

    @Override
    public Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return esProductRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
    }

    @Override
    public Page<EsProduct> searchHighLight(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        HighlightBuilder.Field highlightFieldName = new HighlightBuilder.Field("name")
                .preTags("<span style='color: red'>")
                .postTags("</span>");
        HighlightBuilder.Field highlightFieldSubTitle = new HighlightBuilder.Field("subTitle")
                .preTags("<span style='color: red'>")
                .postTags("</span>");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchQuery("name", keyword));
        boolQueryBuilder.should(QueryBuilders.matchQuery("subTitle", keyword));
        queryBuilder.withQuery(boolQueryBuilder)
                    .withPageable(pageable);
        queryBuilder.withHighlightFields(highlightFieldSubTitle,highlightFieldName);
        //Page<EsProduct> search = esProductRepository.search(queryBuilder.build());
        Page<EsProduct> search = elasticsearchTemplate.queryForPage(queryBuilder.build(),EsProduct.class,new SearchResultMapper(){

            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                List<EsProduct>  esProducts = new ArrayList<EsProduct>();
                //命中记录
                SearchHits hits = response.getHits();
                if(hits.totalHits<0){
                    System.out.println("searchHighLight ：es没有查询到数据");
                    return null;
                }
                for(SearchHit hit : hits){
                    Map<String,Object> hitMap =  hit.getSourceAsMap();
                    EsProduct esProduct = new EsProduct();
                    esProduct.setId(Long.parseLong(hit.getId()));
                    esProduct.setName(hit.getSourceAsMap().get("name")+"");
                    esProduct.setProductSn(hitMap.get("productSn")+"");
                    esProduct.setBrandId(Long.parseLong(hitMap.get("brandId")+""));
                    esProduct.setProductCategoryId(Long.parseLong(hitMap.get("productCategoryId")+""));
                    esProduct.setProductCategoryName(hitMap.get("productCategoryName")+"");
                    esProduct.setPic(hitMap.get("pic")+"");
                    esProduct.setKeywords(hitMap.get("keywords")+"");
                    esProduct.setPrice(BigDecimal.valueOf((Double) hitMap.get("price")));
                    esProduct.setSale(Integer.parseInt(hitMap.get("sale")+""));
                    esProduct.setNewStatus(Integer.parseInt(hitMap.get("newStatus")+""));
                    esProduct.setRecommandStatus(Integer.parseInt(hitMap.get("recommandStatus")+""));
                    esProduct.setStock(Integer.parseInt(hitMap.get("stock")+""));
                    esProduct.setPromotionType(Integer.parseInt(hitMap.get("promotionType")+""));
                    esProduct.setSort(Integer.parseInt(hitMap.get("sort")+""));

                    //高亮字段设置
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    HighlightField highlightField_name = highlightFields.get("name");//姓名
                    HighlightField highlightField_subTitle = highlightFields.get("subTitle");//描述
                    String highLightMessage = "";
                    if(highlightField_name != null){
                        highLightMessage = highlightField_name.fragments()[0].toString();
                        esProduct.setName(highLightMessage);
                    }
                    if(highlightField_subTitle != null){
                        highLightMessage = highlightField_subTitle.fragments()[0].toString();
                        esProduct.setSubTitle(highLightMessage);
                    }
                    esProducts.add(esProduct);
                }
                return new AggregatedPageImpl<>((List<T>)esProducts);
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });
        return search;
    }

    @Override
    public Page<EsProduct> searchDemo(String keyword) {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", keyword);
        SearchQuery searchQuery =new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        Page<EsProduct> search = esProductRepository.search(searchQuery);
        return search;
    }

}
