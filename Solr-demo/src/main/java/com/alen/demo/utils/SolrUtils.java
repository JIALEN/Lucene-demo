package com.alen.demo.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

/**
 * 工具类
 *
 * @author alen
 * @create 2017-10-24 16:04
 **/
public class SolrUtils {
    /**
     * solr 服务器访问地址
     */
    private static String url = "http://localhost:9090/solr/new_core";

    private static Integer connectionTimeout = 100; // socket read timeout

    private static Integer defaltMaxConnectionsPerHost = 100;

    private static Integer maxTotalConnections = 100;

    private static Boolean followRedirects = false; // defaults to false
    private static Boolean allowCompression = true;
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SolrUtils.class);

    /**
     * @param map key is filed name value,map value is filed value
     * @return SolrInputDocument
     */
    public static SolrInputDocument addFileds(Map<String, Object> map, SolrInputDocument document) {

        if (document == null) {
            document = new SolrInputDocument();
        }
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            document.setField(key, map.get(key));
        }
        return document;
    }

    /**
     * 建立solr链接，获取 HttpSolrClient
     *
     * @return HttpSolrClient
     */
    public static HttpSolrClient connect() {

        HttpSolrClient httpSolrClient = null;
        try {
            httpSolrClient = new HttpSolrClient.Builder(url).build();
            httpSolrClient.setParser(new XMLResponseParser());//设定xml文档解析器
            httpSolrClient.setConnectionTimeout(connectionTimeout);//socket read timeout
//            httpSolrClient.setAllowCompression(allowCompression);
//            httpSolrClient.setMaxTotalConnections(maxTotalConnections);
//            httpSolrClient.setDefaultMaxConnectionsPerHost(defaltMaxConnectionsPerHost);
            httpSolrClient.setFollowRedirects(followRedirects);
        } catch (SolrException e) {
            System.out.println("请检查tomcat服务器或端口是否开启!");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return httpSolrClient;
    }

    /**
     * 将SolrDocument 转换为Bean
     *
     * @param record
     * @param clazz
     * @return bean
     */
    public static Object toBean(SolrDocument record, Class clazz) {
        Object obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Object value = record.get(field.getName());
            try {
                BeanUtils.setProperty(obj, field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }
}
