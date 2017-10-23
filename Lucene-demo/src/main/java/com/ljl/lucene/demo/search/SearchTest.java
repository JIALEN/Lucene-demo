package com.ljl.lucene.demo.search;


import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SearchTest {

	private Directory dir;
	private IndexReader reader;
	private IndexSearcher is;

	@Before
	public void setUp() throws Exception {
		dir=FSDirectory.open(Paths.get("D:\\lucene\\index"));
		reader=DirectoryReader.open(dir);
		is=new IndexSearcher(reader);
	}

	@After
	public void tearDown() throws Exception {
		reader.close();
	}

	/**
	 * 对特定项搜索
	 *  按词条搜索—TermQuery
	  *TermQuery是最简单、也是最常用的Query。TermQuery可以理解成为“词条搜索”，
	 * 在搜索引擎中最基本的搜索就是在索引中搜索某一词条，而TermQuery就是用来完成这项工作的。
	  * 在Lucene中词条是最基本的搜索单位，从本质上来讲一个词条其实就是一个名/值对。
	 * 只不过这个“名”是字段名，而“值”则表示字段中所包含的某个关键字。
	 * @throws Exception
	 */
	@Test
	public void testTermQuery()throws Exception{
		String searchField="contents";
		String q="xxxxxxxxx$";
		Term t=new Term(searchField,q);
		Query query=new TermQuery(t);
		TopDocs hits=is.search(query, 10);
		System.out.println("匹配 '"+q+"'，总共查询到"+hits.totalHits+"个文档");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

	/**
	 * “多条件查询”搜索—BooleanQuery
	 * BooleanQuery也是实际开发过程中经常使用的一种Query。
	 * 它其实是一个组合的Query，在使用时可以把各种Query对象添加进去并标明它们之间的逻辑关系。
	 * 在本节中所讨论的所有查询类型都可以使用BooleanQuery综合起来。
	 * BooleanQuery本身来讲是一个布尔子句的容器，它提供了专门的API方法往其中添加子句，
	 * 并标明它们之间的关系，以下代码为BooleanQuery提供的用于添加子句的API接口：
	 * @throws Exception
	 */
	@Test
	public void testBooleanQuery()throws Exception{
		String searchField="contents";
		String q1="xxxxxxxxx";
		String q2="oooooooooooooooo";
		Query query1=new TermQuery(new Term(searchField,q1));
		Query query2=new TermQuery(new Term(searchField,q2));
		BooleanQuery.Builder  builder=new BooleanQuery.Builder();
		//  1．MUST和MUST：取得连个查询子句的交集。
		//  2．MUST和MUST_NOT：表示查询结果中不能包含MUST_NOT所对应得查询子句的检索结果。
		// 3．SHOULD与MUST_NOT：连用时，功能同MUST和MUST_NOT。
		// 4．SHOULD与MUST连用时，结果为MUST子句的检索结果,但是SHOULD可影响排序。
		// 5．SHOULD与SHOULD：表示“或”关系，最终检索结果为所有检索子句的并集。
		// 6．MUST_NOT和MUST_NOT：无意义，检索无结果。
		builder.add(query1, BooleanClause.Occur.MUST);
		builder.add(query2, BooleanClause.Occur.MUST);
		BooleanQuery  booleanQuery=builder.build();
		TopDocs hits=is.search(booleanQuery, 10);
		System.out.println("匹配 "+q1 +"And"+q2+"，总共查询到"+hits.totalHits+"个文档");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

	/**
	 * TermRangeQuery 范围查询
	 *TermRangeQuery是用于字符串范围查询的，既然涉及到范围必然需要字符串比较大小，
	 * 字符串比较大小其实比较的是ASC码值，即ASC码范围查询。
	 * 一般对于英文来说，进行ASC码范围查询还有那么一点意义，
	 * 中文汉字进行ASC码值比较没什么太大意义，所以这个TermRangeQuery了解就行，
	 * 用途不太大，一般数字范围查询NumericRangeQuery用的比较多一点，
	 * 比如价格，年龄，金额，数量等等都涉及到数字，数字范围查询需求也很普遍。
	 * @throws Exception
	 */
	@Test
	public void testTermRangeQuery()throws Exception{
		String searchField="contents";
		String q="1000001----1000002";
		String lowerTermString = "1000001";
		String upperTermString = "1000003";
		/**
		 * field  字段
		 * lowerterm -范围的下端的文字
		 *upperterm -范围的上限内的文本
		 *includelower -如果真的lowerterm被纳入范围。
		 *includeupper -如果真的upperterm被纳入范围。
		 *https://yq.aliyun.com/articles/45353
		 */
		Query query=new TermRangeQuery(searchField,new BytesRef(lowerTermString),new BytesRef(upperTermString),true,true);
		TopDocs hits=is.search(query, 10);
		System.out.println("匹配 '"+q+"'，总共查询到"+hits.totalHits+"个文档");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}


	/**
	 * PrefixQuery  PrefixQuery用于匹配其索引开始以指定的字符串的文档。就是文档中存在xxx%
	 *
	 * @throws Exception
	 */
	@Test
	public void testPrefixQuery()throws Exception{
		String searchField="contents";
		String q="1license";
		Term t=new Term(searchField,q);
		Query query=new PrefixQuery(t);
		TopDocs hits=is.search(query, 10);
		System.out.println("匹配 '"+q+"'，总共查询到"+hits.totalHits+"个文档");

		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

	/**
	 *  所谓PhraseQuery，就是通过短语来检索，比如我想查“big car”这个短语，
	 *  那么如果待匹配的document的指定项里包含了"big car"这个短语，
	 *  这个document就算匹配成功。可如果待匹配的句子里包含的是“big black car”，
	 *  那么就无法匹配成功了，如果也想让这个匹配，就需要设定slop，
	 *  先给出slop的概念：slop是指两个项的位置之间允许的最大间隔距离
	 * @throws Exception
	 */
	@Test
	public void testPhraseQuery()throws Exception{
		String searchField="contents";
		String q1="xxxx";
		String q2="bbb";
		Term t1=new Term(searchField,q1);
		Term t2=new Term(searchField,q2);
		PhraseQuery.Builder builder=new PhraseQuery.Builder();
		builder.add(t1);
		builder.add(t2);
		builder.setSlop(0);
		PhraseQuery query=builder.build();
		TopDocs hits=is.search(query, 10);
		System.out.println("匹配 '"+q1+q2+"之间的几个字段"+"，总共查询到"+hits.totalHits+"个文档");

		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}


	/**
	 * 相近词语的搜索—FuzzyQuery
	 * FuzzyQuery是一种模糊查询，它可以简单地识别两个相近的词语。
	 * @throws Exception
	 */
	@Test
	public void testFuzzyQuery()throws Exception{
		String searchField="contents";
		String q="ljlxx";
		Term t=new Term(searchField,q);
		Query query=new FuzzyQuery(t);
		TopDocs hits=is.search(query, 10);
		System.out.println("匹配 '"+q+"'，总共查询到"+hits.totalHits+"个文档");

		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

	/**
	 * 使用通配符搜索—WildcardQuery
	 * Lucene也提供了通配符的查询，这就是WildcardQuery。
	 * 通配符“?”代表1个字符，而“*”则代表0至多个字符。
	 * @throws Exception
	 */
	@Test
	public void testWildcardQuery()throws Exception{
		String searchField="contents";
		String q="bb??qq";
		Term t=new Term(searchField,q);
		Query query=new WildcardQuery(t);
		TopDocs hits=is.search(query, 10);
		System.out.println("匹配 '"+q+"'，总共查询到"+hits.totalHits+"个文档");

		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

	/**
	 * 解析查询表达式
	 * QueryParser实际上就是一个解析用户输入的工具，可以通过扫描用户输入的字符串，生成Query对象，以下是一个代码示例：
	 * @throws Exception
	 */
	@Test
	public void testQueryParser()throws Exception{
		Analyzer analyzer=new StandardAnalyzer(); // 标准分词器
		String searchField="contents";
		String q="xxxxxxxxx$";
		//指定搜索字段和分析器
		QueryParser parser=new QueryParser(searchField, analyzer);
		//用户输入内容
		Query query=parser.parse(q);
		TopDocs hits=is.search(query, 100);
		System.out.println("匹配 "+q+"查询到"+hits.totalHits+"个记录");
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}


}
