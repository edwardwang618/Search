package com.solrtest;

import com.solrtest.bean.Products;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.StringUtils;
import org.apache.solr.common.params.SolrParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductsTest {
    
    String baseSolrUrl;
    HttpSolrClient client;
    
    @Before
    public void init() {
        baseSolrUrl = "http://localhost:8081/solr/core_demo";
        client = new HttpSolrClient.Builder(baseSolrUrl).build();
    }
    
    @After
    public void commitAndClose() throws IOException, SolrServerException {
        client.commit();
        client.close();
    }
    
    @Test
    public void saveOrUpdate() throws IOException, SolrServerException {
        Products p = new Products();
        // If id exists, then update. Otherwise save.
        p.setPid("999998");
        p.setPname("Test solr");
        p.setCatalogName("Test catalog");
        p.setPrice(21.1);
        p.setDescription("Test description");
        p.setPicture("aa.jpg");
        UpdateResponse response = client.addBean(p);
    
        System.out.println(response);
    }
    
    @Test
    public void delete() throws IOException, SolrServerException {
        UpdateResponse response = client.deleteById("999998");

//        List<String> deleteIds = new ArrayList<>();
//        deleteIds.add("999999");
//        deleteIds.add("999998");
//        UpdateResponse response = client.deleteById(deleteIds);
    
    
//        UpdateResponse response = client.deleteByQuery("prod_pname:Test solr");
//        UpdateResponse response = client.deleteByQuery("prod_price:[20 TO *]");
    
        System.out.println(response);
    }
    
    @Test
    public void simpleQuery() throws IOException, SolrServerException {
        String q = "*:*";
        SolrParams queryParam = new SolrQuery(q);
        QueryResponse queryResponse = client.query(queryParam);
    
        List<Products> list = queryResponse.getBeans(Products.class);
    
        // Default get 10 results
        System.out.println(list.size());
        list.forEach(prod -> System.out.println(prod.getPid() + " " + prod.getPname()));
    
//        System.out.println("Delete successfully");
    }
    
    @Test
    public void complexQuery() throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
    
        String keyWord = "手机";
//        String keyWord = "";
        
        // Set q
//        if (StringUtils.isEmpty(keyWord)) {
//            query.set("q", "*:*");
//        } else {
//            query.set("q", "prod_pname:" + keyWord);
//        }
    
        // With df set, we can omit the field name.
        if (StringUtils.isEmpty(keyWord)) {
            query.set("q", "*");
        } else {
            query.set("q", keyWord);
        }
        
        // Filter the catalog
        String catalogName = "手机饰品";
        if (!StringUtils.isEmpty(catalogName)) {
            query.addFilterQuery("prod_catalog_name:" + catalogName);
        }
        
        String priceStr = "10-";
        if (!StringUtils.isEmpty(priceStr)) {
            String[] arrs = priceStr.split("-");
            if (arrs.length == 1) {
                query.addFilterQuery("prod_price:[" + arrs[0] + " TO *]");
            } else if ("".equals(arrs[0])) {
                query.addFilterQuery("prod_price:[* TO " + arrs[1] + "]");
            } else {
                query.addFilterQuery("prod_price:[" + arrs[0] + " TO " + arrs[1] + "]");
            }
        }
        
        // Sort the price. psort = 1, increasing; psort = 2, decreasing.
        int psort = 0;
        if (psort == 1) {
            query.addSort("prod_price", SolrQuery.ORDER.asc);
        } else if (psort == 0) {
            query.addSort("prod_price", SolrQuery.ORDER.desc);
        }
        
        // Set start and rows
        query.setStart(0);
        query.setRows(6);
        
        // Only show the data with fields included. Others being null.
//        query.setFields("prod_pname", "prod_catalog_name");
        
        // Set df, default field
        query.set("df", "prod_pname");
        
        // Set highlighting
        query.setHighlight(true);
        query.addHighlightField("prod_pname");
        query.setHighlightSimplePre("<font color='red'>");
        query.setHighlightSimplePost("</font>");
    
        QueryResponse queryResponse = client.query(query);
    
        Map<String, Map<String, List<String>>> highlightingMap = queryResponse.getHighlighting();
        
        List<Products> list = queryResponse.getBeans(Products.class);
    
        // Default get 10 results
        System.out.println(list.size());
//        list.forEach(prod -> System.out.println(prod.getPid() + " " + prod.getPname() + " "
//                + prod.getCatalogName() + "" + prod.getPrice()));
    
        for (Products prod : list) {
            String id = prod.getPid();
            Map<String, List<String>> listMap = highlightingMap.get(id);
            List<String> stringList = listMap.get("prod_pname");
            
            // If keyWord is not specified, nothing will be highlighted, which
            // will cause stringList to be null
            if (stringList != null) {
                System.out.println(prod.getPid() + " " + stringList.get(0) + " "
                        + prod.getCatalogName() + "" + prod.getPrice());
            } else {
                System.out.println(prod.getPid() + " " + prod.getPname() + " "
                        + prod.getCatalogName() + "" + prod.getPrice());
            }
        }
        
    }
}
