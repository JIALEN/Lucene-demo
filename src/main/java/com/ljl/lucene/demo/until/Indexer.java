package com.ljl.lucene.demo.until;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

public class Indexer {

	private IndexWriter writer; // 写索引实例

	/**
	 * 构造方法 实例化IndexWriter
	 * @param indexDir
	 * @throws Exception
	 */
	public Indexer(String indexDir)throws Exception{
		Directory dir=FSDirectory.open(Paths.get(indexDir));
		Analyzer analyzer=new StandardAnalyzer(); // 标准分词器
		IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
		writer=new IndexWriter(dir, iwc);
	}

	/**
	 * 关闭写索引
	 * @throws Exception
	 */
	public void close()throws Exception{
		writer.close();
	}

	/**
	 * 索引指定目录的所有文件
	 * @param dataDir
	 * @throws Exception
	 */
	public int index(String dataDir)throws Exception{
		File []files=new File(dataDir).listFiles();
		for(File f:files){
			indexFile(f);
		}
		return writer.numDocs();
	}

	/**
	 * 索引指定文件
	 * @param f
	 */
	private void indexFile(File f) throws Exception{
		System.out.println("索引文件："+f.getCanonicalPath());
		Document doc=getDocument(f);
		writer.addDocument(doc);
	}

	/**
	 * 获取文档，文档里再设置每个字段
	 * @param f
	 */
	private Document getDocument(File f)throws Exception {
		Document doc=new Document();
		doc.add(new TextField("contents",new FileReader(f)));
		doc.add(new TextField("fileName", f.getName(),Field.Store.YES));
		doc.add(new TextField("fullPath",f.getCanonicalPath(),Field.Store.YES));
		return doc;
	}

	public static void main(String[] args) {
		String indexDir="D:\\lucene\\index";
		String dataDir="D:\\lucene\\data";
		Indexer indexer=null;
		int numIndexed=0;
		long start=System.currentTimeMillis();
		try {
			indexer = new Indexer(indexDir);
			numIndexed=indexer.index(dataDir);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				indexer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long end=System.currentTimeMillis();
		System.out.println("索引："+numIndexed+" 个文件 花费了"+(end-start)+" 毫秒");
	}
}
