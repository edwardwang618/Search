package com.solrtest;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SolrTestCRUD {

    String solrUrl;
    HttpSolrClient solrClient;

    /**
     * Initialize the url and the solrClient to do the CRUD operations.
     */
    @BeforeEach
    public void init() {
        solrUrl = "http://localhost:8983/solr/lhc_core";
        solrClient = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
//        System.out.println(solrClient);
    }

    /**
     * Test to add documents. Create documents by initializing their fields,
     * then add them into a list. solrClient will add the documents into
     * solr index.
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void testAddDocument() throws IOException, SolrServerException {
        SolrInputDocument document1 = new SolrInputDocument();

        document1.addField("id", "1");
        document1.addField("name", "foot ball");
        document1.addField("size", "1.2");

        SolrInputDocument document2 = new SolrInputDocument();
        document2.addField("id", "2");
        document2.addField("name", "basket ball");
        document2.addField("size", "2.1");

        SolrInputDocument document3 = new SolrInputDocument();
        document3.addField("id", "3");
        document3.addField("name", "table tennis");
        document3.addField("size", "0.4");

        SolrInputDocument document4 = new SolrInputDocument();
        document4.addField("id", "4");
        document4.addField("name", "tennis");
        document4.addField("size", "1.0");

        List<SolrInputDocument> docs = new ArrayList<>();

        docs.add(document1);
        docs.add(document2);
        docs.add(document3);
        docs.add(document4);

        solrClient.add(docs);
        solrClient.commit();
        solrClient.close();
    }

    /**
     * Add the ids of documents that needed to be deleted into a list,
     * then solrClient will delete the corresponding documents in the
     * index.
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void testDeleteById() throws IOException, SolrServerException {
        List<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("2");
        ids.add("3");
        ids.add("4");

        solrClient.deleteById(ids);
        solrClient.commit();
        solrClient.close();
    }

    /**
     * Delete the documents by first doing the query, then remove them.
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void testDeleteByQuery() throws IOException, SolrServerException {
        // Query the documents whose name contains ball, then delete them.
        solrClient.deleteByQuery("name:ball");
        solrClient.commit();
        solrClient.close();
    }

    /**
     * Query the documents by specifying the condition.
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void testQuery() throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        // Set the query object to query the documents whose names contain "ball"
        query.setQuery("name:ball");
//        query.setQuery("*:*");
//        query.set("q", "name:ball");
        QueryResponse response = solrClient.query(query);
        SolrDocumentList results = response.getResults();

        long numFound = results.getNumFound();
        System.out.println("Total hits = " + numFound);

        for (SolrDocument solrDocument : results) {
            System.out.println("id:" + solrDocument.get("id"));
            System.out.println("name: " + solrDocument.get("name"));
            System.out.println("size: " + solrDocument.get("size"));
        }

        solrClient.close();
    }

    /**
     * To update the document, we need to new a SolrInputDocument, set the
     * id to be the id of the document that we want to update. Then use
     * solrClient to do the modification.
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void updateIndex() throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "1");
        doc.addField("name", "water ball");
        doc.addField("size", "2.0");

        solrClient.add(doc);
        solrClient.commit();
        solrClient.close();
    }

}
