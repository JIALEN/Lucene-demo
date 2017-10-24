package com.alen.lucene.demo.until;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * 测试加权
 */
public class FieldWeighing {
    private String ids[]={"1","2","3","4"};
    private String authors[]={"Jack","Marry","John","Json"};
    private String positions[]={"accounting","technician","salesperson","boss"};
    private String titles[]={"Java is a good language.","Java is a cross platform language","Java powerful","You should learn java"};
    private String contents[]={
            "If possible, use the same JRE major version at both index and search time.",
            "When upgrading to a different JRE major version, consider re-indexing. ",
            "Different JRE major versions may implement different versions of Unicode,",
            "For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6,"
    };

    private Directory dir;

    /**
     * 获取IndexWriter实例
     * @return
     * @throws Exception
     */
    private IndexWriter getWriter()throws Exception{
        Analyzer analyzer=new StandardAnalyzer(); // 标准分词器
        IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
        IndexWriter writer=new IndexWriter(dir, iwc);
        return writer;
    }

    /**
     * 生成索引
     * @throws Exception
     */
    @Test
    public void index()throws Exception{
        dir=FSDirectory.open(Paths.get("D:\\lucence\\index03"));
        IndexWriter writer=getWriter();
        for(int i=0;i<ids.length;i++){
            Document doc=new Document();
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("author",authors[i],Field.Store.YES));
            doc.add(new StringField("position",positions[i],Field.Store.YES));
            TextField field=new TextField("title", titles[i], Field.Store.YES);
            doc.add(field);
            doc.add(new TextField("content", contents[i], Field.Store.NO));
            writer.addDocument(doc); // 添加文档
        }
        writer.close();
    }

    /**
     * 查询
     * @throws Exception
     */
    @Test
    public void search()throws Exception{
        dir=FSDirectory.open(Paths.get("D:\\lucence\\index03"));
        IndexReader reader=DirectoryReader.open(dir);
        IndexSearcher is=new IndexSearcher(reader);
//        String searchField="title";
//        String q="java";
//        Term t=new Term(searchField,q);
//        Query query=new TermQuery(t);

        //查询 索引域 中的 file_name , file_content
        Query q1 = new TermQuery(new Term("title" ,"java"));
        Query q2 = new TermQuery(new Term("author","Json"));
       //将 q1 设置 Boost 值
        BoostQuery q3 = new BoostQuery(q2,100f);
     //复合语句查询
        BooleanQuery.Builder  builder =  new BooleanQuery.Builder();
        builder.add(q1, BooleanClause.Occur.MUST);
        builder.add(q3, BooleanClause.Occur.MUST);
       //由于 file_name 的查询语句经过 BoostQuery 的包装
        //因此 file_name 的优先级更高。
        BooleanQuery query = builder.build();
        TopDocs hits=is.search(query, 10);
        System.out.println("匹配 java总共查询到"+hits.totalHits+"个文档");
        for(ScoreDoc scoreDoc:hits.scoreDocs){
            Document doc=is.doc(scoreDoc.doc);
            System.out.println(doc.get("author"));
        }
        reader.close();
    }

}
