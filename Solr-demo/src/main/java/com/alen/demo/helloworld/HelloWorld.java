package com.alen.demo.helloworld;

import com.alen.demo.utils.SolrUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Map;

/**
 * helloworld
 *
 * @author alen
 * @create 2017-10-24 15:45
 **/
public class HelloWorld {


    /**
     * 简单查询
     *
     * @param mQueryStr
     * @return query result
     */
    public SolrDocumentList query(String mQueryStr) {
        try {
            HttpSolrClient httpSolrClient = SolrUtils.connect();
            SolrQuery query = new SolrQuery();
            //设定查询字段
            query.setQuery(mQueryStr);
            //指定返回结果字段
            query.set("fl", "id,name");
            //覆盖schema.xml的defaultOperator（有空格时用"AND"还是用"OR"操作逻辑），一般默认指定。必须大写
            query.set("q.op", "AND");
            //设定返回记录数，默认为10条
            query.setRows(10);
            QueryResponse response = httpSolrClient.query(query);
            SolrDocumentList list = response.getResults();
            return list;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param queryStr
     * @param start
     * @param rows
     * @return SolrDocumentList
     */
    public SolrDocumentList queryPage(String queryStr, Integer start, Integer rows) {
        try {
            HttpSolrClient httpSolrClient = SolrUtils.connect();
            SolrQuery query = new SolrQuery();
            //设定查询字段
            query.setQuery(queryStr);
            //指定返回结果字段
            query.setIncludeScore(true);
            // query.set("fl","id,name");
            //覆盖schema.xml的defaultOperator（有空格时用"AND"还是用"OR"操作逻辑），一般默认指定。必须大写
            query.set("q.op", "AND");
            //分页开始页数
            query.setStart(start);
            //设定返回记录数，默认为10条
            query.setRows(rows);
            //设定对查询结果是否高亮
            query.setHighlight(true);
            //设定高亮字段前置标签
            query.setHighlightSimplePre("<span style=\"color:red\">");
            //设定高亮字段后置标签
            query.setHighlightSimplePost("</span>");
            //设定高亮字段
            query.addHighlightField("name");
            //设定拼写检查
            query.setRequestHandler("/spell");
            QueryResponse response = httpSolrClient.query(query);
            //获取bean
            //  List<Object> bean = response.getBeans(Object.class);
            SolrDocumentList list = response.getResults();
            return list;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 添加一个实体
     *
     * @param object
     */
    public void addBean(Object object) {
        try {
            HttpSolrClient httpSolrClient = SolrUtils.connect();
            httpSolrClient.addBean(object);
            httpSolrClient.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

    }

    /**
     * 添加简单索引
     *
     * @param map
     */
    public void addDoc(Map<String, Object> map) {
        try {
            HttpSolrClient httpSolrClient = SolrUtils.connect();
            SolrInputDocument document = new SolrInputDocument();
            document = SolrUtils.addFileds(map, document);
            UpdateResponse response = httpSolrClient.add(document);
            httpSolrClient.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除索引
     *
     * @param id
     */
    public void deleteById(String id) {
        try {
            HttpSolrClient httpSolrClient = SolrUtils.connect();
            httpSolrClient.deleteById(id);
            httpSolrClient.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}