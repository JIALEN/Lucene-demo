package com.alen.lucene.demo.curd;

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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

public class IndeCURD01 {

	private String ids[]={"1","2","3"};
	private String citys[]={"qingdao","nanjing","shanghai"};
	private String descs[]={
			"Qingdao is a beautiful city.",
			"Nanjing is a city of culture.",
			"Shanghai is a bustling city."
	};

	private Directory dir;

	@Before
	public void setUp() throws Exception {
		dir=FSDirectory.open(Paths.get("D:\\lucence\\index02"));
		IndexWriter writer=getWriter();
		for(int i=0;i<ids.length;i++){
			Document doc=new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("city",citys[i],Field.Store.YES));
			doc.add(new TextField("desc", descs[i], Field.Store.NO));
			writer.addDocument(doc); // 添加文档
		}
		writer.close();
	}

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
	 * 测试写了几个文档
	 * @throws Exception
	 */
	@Test
	public void testIndexWriter()throws Exception{
		IndexWriter writer=getWriter();
		System.out.println("写入了"+writer.numDocs()+"个文档");
		writer.close();
	}

	/**
	 * 测试读取文档
	 * @throws Exception
	 */
	@Test
	public void testIndexReader()throws Exception{
		IndexReader reader=DirectoryReader.open(dir);
		System.out.println("最大文档数："+reader.maxDoc());
		System.out.println("实际文档数："+reader.numDocs());
		reader.close();
	}

	/**
	 * 测试删除 在合并前
	 * 使用deleteDocuments删除后实际文档数减一，其实就是做了个标记未真正
	 * 删除，最大文档数未变
	 * @throws
	 */
	@Test
	public void testDeleteBeforeMerge()throws Exception{
		IndexWriter writer=getWriter();
		System.out.println("删除前："+writer.numDocs());
		writer.deleteDocuments(new Term("id","1"));// 强制删除此时删除的文档并不会被完全删除，而是存储在一个回收站中的，可以恢复
		writer.commit();// writer.commit(); //更改索引要提交，和提交数据库事务一个概念，真正的删除
		System.out.println("writer.maxDoc()："+writer.maxDoc());
		System.out.println("writer.numDocs()："+writer.numDocs());
		writer.close();
	}

	/**
	 * 测试删除 在合并后
	 * 使用forceMergeDeletes强制删除
	 * 删除后实际文档数减一，最大文档数减一
	 * @throws Exception
	 */
	@Test
	public void testDeleteAfterMerge()throws Exception{
		IndexWriter writer=getWriter();
		System.out.println("删除前："+writer.numDocs());
		writer.deleteDocuments(new Term("id","1"));// 强制删除此时删除的文档并不会被完全删除，而是存储在一个回收站中的，可以恢复
		writer.forceMergeDeletes(); //强制合并删除的索引信息，索引量大的时候不推荐使用，真正的删除
		// writer.commit(); //更改索引要提交，和提交数据库事务一个概念，真正的删除
		writer.commit();
		System.out.println("writer.maxDoc()："+writer.maxDoc());
		System.out.println("writer.numDocs()："+writer.numDocs());
		writer.close();
	}

	/**
	 * 测试更新
	 * 实际上就是删除后新增一条
	 * @throws Exception
	 */
	@Test
	public void testUpdate()throws Exception{
		IndexWriter writer=getWriter();
		Document doc=new Document();
		doc.add(new StringField("id", "1", Field.Store.YES));
		doc.add(new StringField("city","qingdao",Field.Store.YES));
		doc.add(new TextField("desc", "dsss is a city.", Field.Store.NO));
		writer.updateDocument(new Term("id","1"), doc);
		writer.close();
	}
}
