package com.luceneDemo.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * To test the query of index.
 */
public class SearchIndex {
    
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;
    
    @BeforeEach
    public void init() throws IOException {
        indexReader = DirectoryReader.open(FSDirectory.open(new File("index").toPath()));
        indexSearcher = new IndexSearcher(indexReader);
    }
    
    @Test
    public void testRangeQuery() throws IOException {
        // Query the document whose field size ranges from 100L to 2000L
        Query query = LongPoint.newRangeQuery("size", 100L, 2_000L);

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
    }

    public void printResult(Query query) throws IOException {
        // Find the top 10 hits for the query
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("Total hits number: " + topDocs.totalHits + "\n");

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;

            // Get the document from the document Id
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("name"));
            System.out.println(doc.get("size"));
            System.out.println("==================");
        }
    }

}
