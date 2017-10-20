//package com.ljl.lucene.demo.until;
//
//import com.ljl.lucene.demo.constants.LuceneConstants;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.index.CorruptIndexException;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.util.Version;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.io.FileReader;
//import java.io.IOException;
//
///**
// * 用于索引的原始数据，这样我们就可以使用Lucene库，使其可搜索。
// *
// * @author lijialun
// * @create 2017-10-20 16:41
// **/
//public class Indexer {
//    private IndexWriter writer;
//
//    public Indexer(String indexDirectoryPath) throws IOException {
//        //this directory will contain the indexes
//        try {
//        Directory indexDirectory =
//                FSDirectory.open(new File(indexDirectoryPath));
//
//        //create the indexer
//        writer = new IndexWriter(indexDirectory,
//                new StandardAnalyzer(Version.LUCENE_36),true,
//                IndexWriter.MaxFieldLength.UNLIMITED);
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//    }
//
//    public void close() throws CorruptIndexException, IOException{
//        writer.close();
//    }
//
//    private Document getDocument(File file) throws IOException{
//        Document document = new Document();
//
//        //index file contents
//        Field contentField = new Field(LuceneConstants.CONTENTS,
//                new FileReader(file));
//        //index file name
//        Field fileNameField = new Field(LuceneConstants.FILE_NAME,
//                file.getName(),
//                Field.Store.YES,Field.Index.NOT_ANALYZED);
//        //index file path
//        Field filePathField = new Field(LuceneConstants.FILE_PATH,
//                file.getCanonicalPath(),
//                Field.Store.YES,Field.Index.NOT_ANALYZED);
//
//        document.add(contentField);
//        document.add(fileNameField);
//        document.add(filePathField);
//
//        return document;
//    }
//
//    private void indexFile(File file) throws IOException{
//        System.out.println("Indexing "+file.getCanonicalPath());
//        Document document = getDocument(file);
//        writer.addDocument(document);
//    }
//
//    public int createIndex(String dataDirPath, FileFilter filter)
//            throws IOException{
//        //get all files in the data directory
//        File[] files = new File(dataDirPath).listFiles();
//
//        for (File file : files) {
//            if(!file.isDirectory()
//                    && !file.isHidden()
//                    && file.exists()
//                    && file.canRead()
//                    && filter.accept(file)
//                    ){
//                indexFile(file);
//            }
//        }
//        return writer.numDocs();
//    }
//}
