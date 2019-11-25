package com.luceneDemo.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * To test the query of index.
 */
public class SearchIndex {
    
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;
    
    @BeforeEach
    public void init() throws IOException {
        indexReader = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
        indexSearcher = new IndexSearcher(indexReader);
    }
    
    @Test
    public void testMatchAllDocsQuery() throws IOException {
        // Simply query all documents
        Query query = new MatchAllDocsQuery();
        
        printResult(query);
        
        indexReader.close();
    }
    
    @Test
    public void testRangeQuery() throws IOException {
        // Query the document whose field size ranges from 100L to 2000L, both included
        Query query = LongPoint.newRangeQuery("size", 100L, 2_000L);
        printResult(query);
        indexReader.close();
    }

    @Test
    public void testBooleanQuery() throws IOException {
        // Add multiple conditions to query
        Builder builder = new Builder();
        
        Query query1 = new TermQuery(new Term("name", "apache"));
        Query query2 = new TermQuery(new Term("name", "lucene"));
        BooleanClause clause = new BooleanClause(query2, Occur.MUST);
        
        builder.add(query1, Occur.SHOULD);
        builder.add(clause);
    
        // Query the documents whose name must contain lucene, should contain apache
        Query query = builder.build();
        printResult(query);
    }
    
    @Test
    public void testQueryParser() throws ParseException, IOException {
        // Create QueryParser object, 2 parameters
        // Parameter1, default search field. Parameter 2, analyzer
        QueryParser queryParser = new QueryParser("name", new IKAnalyzer());
        // Create query whose field is "name", and the keyword is "lucene"
        Query query = queryParser.parse("lucene");
    
        printResult(query);
        indexReader.close();
    }
    
    @Test
    public void testMultiFieldQueryParser() throws ParseException, IOException {
        String[] fields = {"name", "content"};
        QueryParser queryParser = new MultiFieldQueryParser(fields, new IKAnalyzer());
    
        Query query = queryParser.parse("apache lucene");
        
        printResult(query);
    }

    public void printResult(Query query) throws IOException {
        System.out.println("query = " + query);
        // Find the top 3 hits for the query
        TopDocs topDocs = indexSearcher.search(query, 3);
        System.out.println("Total hits number: " + topDocs.totalHits + "\n");

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;

            // Get the document from the document Id
            Document document = indexSearcher.doc(docId);
            System.out.println("name = " + document.get("name"));
            System.out.println("path = " + document.get("path"));
//            System.out.println("content = " + document.get("content"));
            System.out.println("size = " + document.get("size"));
            System.out.println("====================\n");
        }
    }

}
