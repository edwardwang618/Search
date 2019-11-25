package com.luceneDemo.lucene;


import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class LuceneFirst {

    /**
     * Create index for the txt files stored in the "searchsources" directory
     * @throws IOException
     */
    @Test
    public void createIndex() throws IOException {
        File indexLocation = new File("index");
        if (indexLocation.exists()) {
            FileUtils.deleteDirectory(indexLocation);
        }
        indexLocation.mkdir();
        // Create Directory object, set the location where we save the indices
        // RAMDirectory means to save in the memory
        // Directory directory = new RAMDirectory();
        
        // Here we choose to save the indices to local disk path
        Directory directory = FSDirectory.open(indexLocation.toPath());
        
        // create indexWriter
        // IndexWriterConfig default constructor is using StandardAnalyzer
        // Here we need to use IKAnalyzer to handle non-English analysis
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // "searchsource" directory is where the searching documents locate
        File dir = new File("searchsource");
        File[] files = dir.listFiles();

        // If there is no file, exit the method
        if (null == files || 0 == files.length) {
            return;
        }

        for (File file : files) {
            // Get field values
            String fileName = file.getName();
            String filePath = file.getPath();
            String fileContent = FileUtils.readFileToString(file, "utf-8");
            // fileSize equals to how many letters in the file
            long fileSize = FileUtils.sizeOf(file);

            // Create field
            Field fieldName = new TextField("name", fileName, Field.Store.YES);

            // Field fieldPath = new TextField("path", filePath, Store.YES);
            // Stored by default
            Field fieldPath = new StoredField("path", filePath);

            Field fieldContent = new TextField("content", fileContent, Store.YES);

            // LongPoint type field not stored by default, only for doing calculations
            Field fieldSizeValue = new LongPoint("size", fileSize);
            // StoreField type only store, not for calculations
            Field fieldSizeStore = new StoredField("size", fileSize);
            // Create document object
            Document document = new Document();
            
            // Add fields to document
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContent);
            document.add(fieldSizeValue);
            document.add(fieldSizeStore);
            // Write the documents into index
            // When adding the document, the dics will be loaded
            indexWriter.addDocument(document);
    
        }
        
        indexWriter.close();
    }

    /**
     * Execute the search in the index
     * @throws IOException
     */
    @Test
    public void searchIndex() throws IOException {
        // Locate the index directory
        Directory directory = FSDirectory.open(new File("index").toPath());
        
        // Create IndexReader object
        IndexReader indexReader = DirectoryReader.open(directory);
        
        // Create IndexSearcher object
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
        // Create a TermQuery object, specify the field and the keyword
        Query query = new TermQuery(new Term("name", "spring"));
        
        // Execute the search, only want to show the first 5 hits
        TopDocs topDocs = indexSearcher.search(query, 5);
        
        // Get the total number of hits
        System.out.println("Total records: " + topDocs.totalHits + "\n");
        
        // Get the list of docs
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
    
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
    
            Document document = indexSearcher.doc(docId);
            System.out.println("name = " + document.get("name"));
            System.out.println("path = " + document.get("path"));
//            System.out.println("content = " + document.get("content"));
            System.out.println("size = " + document.get("size"));
            System.out.println("====================\n");
        }
    
        indexReader.close();
    }

    /**
     * To test what terms have been analyzed for building the index. This would help
     * decide if the analyzer is good enough to analyze the files to build useful
     * index.
     * @throws IOException
     */
    @Test
    public void testTokenStream() throws IOException {
        // Create an Analyzer object
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        // Create a Tokenstream object with regard to the text
        TokenStream tokenStream;
        tokenStream = analyzer.tokenStream("", "The Spring Framework provides a comprehensive programming and configuration model.");

        // Set a pointer for the tokenStream to get the terms
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        // Set a pointer for the tokenStream to get the starting and ending positions
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        
        // Place the pointer at the beginning of the text
        tokenStream.reset();

        // Loop the token to see what terms have been analyzed to build the index
        while (tokenStream.incrementToken()) {
            System.out.println("start-> " + offsetAttribute.startOffset());
            System.out.println(charTermAttribute);
            System.out.println("end-> " + offsetAttribute.endOffset());
    
            System.out.println();
        }
        
        tokenStream.close();
    }
    
}
