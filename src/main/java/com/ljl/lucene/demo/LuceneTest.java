package com.ljl.lucene.demo;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * luncene测试
 *
 * @author lijialun
 * @create 2017-10-20 14:31
 **/
//@RestController和@
// RequestMapping注解是Spring MVC 的注解（它们不是Spring Boot特有的）
@RestController
//配置Spring,基于你已经添加jar依赖项。如果spring-boot-starter-web已经添加Tomcat和Spring MVC,
// 这个注释自动将假设您正在开发一个web应用程序并添加相应的spring设置。
//自动配置被设计用来和“Starters”一起更好的工作,但这两个概念并不直接相关。您可以自由挑选starter依赖项以外的jar包，springboot仍将尽力自动配置您的应用程序。
//spring通常建议我们将main方法所在的类放到一个root包下，
//@EnableAutoConfiguration（开启自动配置）注解通常都放到main所在类的上面，下面是一个典型的结构布局：
@EnableAutoConfiguration
public class LuceneTest {

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home() {
        return "Hello World!";
    }


    //addDoc()方法把文档（译者注：这里的文档是Lucene中的Document类的实例）添加到索引中。
    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }

    public static void main(String[] args) {
        try {
            //一个默认的过滤器和过滤器
            //参数 过滤掉词条数组数组
            StandardAnalyzer analyzer = new StandardAnalyzer();
            Directory index = new RAMDirectory();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter w = new IndexWriter(index, config);
            addDoc(w, "Lucene in Action", "193398817");
            addDoc(w, "Lucene for Dummies", "55320055Z");
            addDoc(w, "Managing Gigabytes", "55063554A");
            addDoc(w, "The Art of Computer Science", "9900333X");
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
