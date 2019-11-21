package com.luceneDemo.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * To test basic operations we can do on index, add, delete and update
 */
public class IndexManager {
    
    private IndexWriter indexWriter;
    
    @BeforeEach
    public void init() throws IOException {
        indexWriter = new IndexWriter(
                // Specify the path that will store the index
                FSDirectory.open(new File("index").toPath()),
                // Specify the configuration, namely what analyzer will be used
                // Instead of StandardAnalyzer, we use IKAnalyzer which can not only analyze English, but also Chinese
                new IndexWriterConfig(new IKAnalyzer()));
    }
    
    @Test
    public void addDocument() throws IOException {
        
        // Create a new document
        Document document = new Document();
        document.add(new TextField("name", "A New File", Field.Store.YES));
        document.add(new TextField("content", "New File Content", Field.Store.NO));
        document.add(new StoredField("path", "searchsource"));
        
        indexWriter.addDocument(document);
        indexWriter.close();
    }
    
    @Test
    public void deleteAllDocuments() throws IOException {
        indexWriter.deleteAll();
        indexWriter.close();
    }
    
    
    @Test
    public void deleteDocumentByQuery() throws IOException {
        // Delete all documents whose name contains "apache"
        indexWriter.deleteDocuments(new Term("name", "apache"));
        indexWriter.close();
    }
    
    /**
     * To update documents in lucene, is the same as doing the query, delete the results, then
     * add the new document.
     */
    @Test
    public void updateDocument() throws IOException {
        Document document = new Document();
        
        document.add(new TextField("name", "After update", Field.Store.YES));
        document.add(new TextField("name1", "After update1", Field.Store.YES));
        document.add(new TextField("name2", "After update2", Field.Store.YES));

        // What lucene will do is, to query the document whose name contains "spring",
        // delete them, then add the new document (the second parameter "document")
        indexWriter.updateDocument(new Term("name", "spring"), document);
        indexWriter.close();
        
    }
}
