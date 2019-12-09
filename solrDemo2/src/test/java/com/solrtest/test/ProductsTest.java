package com.solrtest.test;

import com.aol.cyclops.streams.StreamUtils;
import com.solrtest.bean.Products;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.StringUtils;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"/spring_solr.xml"})
public class ProductsTest {
    
    @Autowired
    private HttpSolrClient httpSolrClient;
    
    @Test
    public void pingTest() throws IOException, SolrServerException {
        SolrPingResponse ping = httpSolrClient.ping();
        System.out.println(ping.getStatus());
    }
    
    @After
    public void commitAndClose() throws IOException, SolrServerException {
        httpSolrClient.commit();
        httpSolrClient.close();
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
        UpdateResponse response = httpSolrClient.addBean(p);
    
    }
    
    @Test
    public void delete() throws IOException, SolrServerException {
        // UpdateResponse response = httpSolrClient.deleteById("999998");

//        List<String> deleteIds = new ArrayList<>();
//        deleteIds.add("999999");
//        deleteIds.add("999998");
//        UpdateResponse response = httpSolrClient.deleteById(deleteIds);
    
    
       UpdateResponse response = httpSolrClient.deleteByQuery("prod_pname:Test solr");
       // UpdateResponse response = httpSolrClient.deleteByQuery("prod_price:[20 TO *]");
    
        System.out.println(response);
    }
    
    @Test
    public void simpleQuery() throws IOException, SolrServerException {
        String q = "*:*";
        SolrParams queryParam = new SolrQuery(q);
        QueryResponse queryResponse = httpSolrClient.query(queryParam);
    
        List<Products> list = queryResponse.getBeans(Products.class);
    
        // Default get 10 results
        System.out.println(list.size());
        list.forEach(prod -> System.out.println(prod.getPid() + " " + prod.getPname()));
    
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
    
        QueryResponse queryResponse = httpSolrClient.query(query);
    
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
                // Keyword highlighted
                System.out.println(prod.getPid() + " " + stringList.get(0) + " "
                        + prod.getCatalogName() + "" + prod.getPrice());
            } else {
                // Nothing highlighted
                System.out.println(prod.getPid() + " " + prod.getPname() + " "
                        + prod.getCatalogName() + "" + prod.getPrice());
            }
        }
        
    }
    
    @Test
    public void facetQuery() throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        String para = "*:*";
        
        query.setFacet(true);
        query.addFacetField("prod_catalog_name", "prod_pname");
        query.setFacetLimit(100);
        
        query.setFacetSort("count");
        
        // Exclude facet which has not facet value
        query.setFacetMissing(false);
        // Exclude groups with count < 5
        query.setFacetMinCount(5);
        
        query.addFacetQuery("prod_price:[2 TO 10]");
        
        query.setQuery(para);
    
        QueryResponse queryResponse = httpSolrClient.query(query);
    
        List<FacetField> facets = queryResponse.getFacetFields();
        System.out.println(query);
        for (FacetField facet : facets) {
            System.out.println("facet field: " + facet.getName());
            System.out.println("---------------");
            List<FacetField.Count> counts = facet.getValues();
            
            // Sort the count in descending order by default.
            // counts.forEach(count -> System.out.println(count.getName() + ": " + count.getCount()));
            
            // In order to sort the count in ascending order, we need to do it manually
            StreamUtils.reverse(counts.stream()).forEach(count -> System.out.println(count.getName() + ": " + count.getCount()));
        
            System.out.println();
        }
        
    }
    
    @Test
    public void facetPivot() throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        
        String para = "*:*";
        
        query.setFacet(true);
        // Multi-dimensional query
        query.add("facet.pivot", "prod_catalog_name,prod_pname");
        
        query.setFacetLimit(100);
        query.setFacetMinCount(5);
        query.setQuery(para);
    
        // Use post to avoid error if the query url is too long for get request
        QueryResponse queryResponse = httpSolrClient.query(query, SolrRequest.METHOD.POST);
    
        NamedList<List<PivotField>> namedList = queryResponse.getFacetPivot();
    
        // namedList is a tree structure. The first level is prod_catalog_name, the second level is prod_pname.
        // Goods in each prod_catalog_name is grouped with regard to prod_pname
//        System.out.println(namedList);
        
        for (int i = 0; i < namedList.size(); i++) {
            List<PivotField> pivotFields = namedList.getVal(i);
            // Print the first level
            for (PivotField pivotField : pivotFields) {
                System.out.println(pivotField.getValue() + " " + pivotField.getCount());
            }
        }
    }
    
}
