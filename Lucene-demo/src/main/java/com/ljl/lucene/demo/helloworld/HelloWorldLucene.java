package com.ljl.lucene.demo.helloworld;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * 最简单的dmeo
 *
 * @author lijialun
 * @create 2017-10-21 13:54
 **/
public class HelloWorldLucene {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("*****************检索开始**********************");
        // 创建一个内存目录对象，所以这里生成的索引不会放在磁盘中，而是在内存中。
        //public IndexWriter(Directory d, Analyzer a, boolean create)
        //中的Directory类型，在Lucene工具当中有两个子类分别是RAMDirectory 和 FSDirectory
        //这两个目录度可以作为索引的存储路径
        //RAMDirectory是存放到内存当中的一个区域，FSDirectory是存放到文件系统中的磁盘里
        //虽然向其添加Document的过程与使用FSDDirectory一样，但是由于它是内存中的一块区域
        //因此，如果不将RAMDirectory中的内存写入磁盘，当虚拟机退出后，里面的内容也会随之消失。
        //一次需要将RAMDirectory中的内容转到FSDirectory中。
        RAMDirectory directory = new RAMDirectory();
            /*
              创建IndexWriter实例时，通过IndexWriterConfig来设置其相关配置：
              详细：http://blog.itpub.net/28624388/viewspace-766134/
             * public IndexWriterConfig(Analyzer analyzer)
             * analyzer：分词器对象
             *
             *  StandardAnalyzer是lucene中内置的“标准分析器”，可以做如下功能:
            对原有句子按照空格进行了分词
            所有的大写字母都可以能转换为小写的字母
              可以去掉一些没有用处的单词，例如"is","the","are"等单词，也删除了所有的标点
             */
        IndexWriterConfig writerConfig = new IndexWriterConfig(new StandardAnalyzer());
            /*
             *IndexWriter用于更新或创建索引。它不是用来读取索引。
             * 创建索引写入对象，该对象既可以把索引写入到磁盘中也可以写入到内存中。 参数说明：
             * public IndexWriter(Directory directory, IndexWriterConfig conf)
             * directory:目录对象,也可以是FSDirectory 磁盘目录对象
             * conf:写入对象的控制
             */

        IndexWriter writer = new IndexWriter(directory, writerConfig);
        // 创建Document 文档对象，在lucene中创建的索引可以看成数据库中的一张表，
        // 表中也可以有字段,往里面添加内容之后可以根据字段去匹配查询
        // 下面创建的doc对象中添加了三个字段，分别为name,sex,dosomething,
        Document doc = new Document();
            /*
            * 参数说明 public Field(String name, String value, FieldType type)
            * name : 字段名称
            * value : 字段的值 store :
            *  TextField.TYPE_STORED:存储字段值
            */
        doc.add(new Field("name", "lin zhengle", TextField.TYPE_STORED));
        doc.add(new Field("address", "中国上海", TextField.TYPE_STORED));
        doc.add(new Field("dosometing", "I am learning lucene ", TextField.TYPE_STORED));
        writer.addDocument(doc);
        writer.close(); // 这里可以提前关闭，因为dictory 写入内存之后 与IndexWriter 没有任何关系了

        // 因为索引放在内存中，所以存放进去之后要立马测试，否则，关闭应用程序之后就检索不到了
        // 创建IndexSearcher 检索索引的对象，里面要传递上面写入的内存目录对象directory
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(ireader);
        // 根据搜索关键字 封装一个term组合对象，然后封装成Query查询对象
        // dosometing是上面定义的字段，lucene是检索的关键字
        //TermQuery 是 lucene查询中最基本的一种原子查询，从它的名字Term我们可以看出，它只能针对一个字段进行查询。
        //Term 这个类是搜索的最低单位。它是在索引过程中类似Field。建立搜索单元, SearchType代表要搜索的Filed, searchKey代表关键字
        Query query = new TermQuery(new Term("dosometing", "lucene"));
        // Query query = new TermQuery(new Term("address", "中国上海"));
        // Query query = new TermQuery(new Term("name", "cheng"));

        // TopDocs 指向相匹配的搜索条件的前N个搜索结果。它是指针的简单容器指向它们的搜索结果输出的文档。
        // TopDocs 类的字段：
        // ScoreDoc[] scoreDocs -- 排名靠前的查询。
        //int totalHits -- 命中的查询的总数。

        // 去索引目录中查询，返回的是TopDocs对象，里面存放的就是上面放的document文档对象
        TopDocs rs = searcher.search(query, 100);
        long endTime = System.currentTimeMillis();
        System.out.println("总共花费" + (endTime - startTime) + "毫秒，检索到" + rs.totalHits + "条记录。");
        for (int i = 0; i < rs.scoreDocs.length; i++) {
            // rs.scoreDocs[i].doc 是获取索引中的标志位id, 从0开始记录
            Document firstHit = searcher.doc(rs.scoreDocs[i].doc);
            System.out.println("name:" + firstHit.getField("name").stringValue());
            System.out.println("address:" + firstHit.getField("address").stringValue());
            System.out.println("dosomething:" + firstHit.getField("dosometing").stringValue());
        }

        writer.close();
        directory.close();
        System.out.println("*****************检索结束**********************");
    }
}
