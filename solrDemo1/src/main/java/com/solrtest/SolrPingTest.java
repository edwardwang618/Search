package com.solrtest;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.SolrPing;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

public class SolrPingTest {
    
    String solrUrl;
    HttpSolrClient solrClient;
    
    /**
     * Initialize the solrUrl and the solrClient to do the CRUD operations.
     */
    @BeforeEach
    @Test
    public void init() throws IOException {
        // Specify the solr core that we want to do operations
        Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/solr.properties"));
        
        solrUrl = properties.getProperty("url");
        solrClient = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
        
//        System.out.println(solrUrl);
//        System.out.println(solrClient);
    
    }
    
    @Test
    public void testPing() throws IOException, SolrServerException {
        SolrPingResponse resp = solrClient.ping();
        int status = resp.getStatus();
        System.out.println(status);
    
    }
}
