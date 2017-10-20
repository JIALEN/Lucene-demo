package com.ljl.lucene.demo.filter;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * 过滤器
 *
 * @author lijialun
 * @create 2017-10-20 16:36
 **/
public class TextFileFilter extends FileFilter {
    /**
     * Whether the given file is accepted by this filter.
     *
     * @param f
     */
    @Override
    public boolean accept(File f) {
     // endsWith() 该方法返回一个true，如果参数表示的字符序列是由该对象表示的字符序列的后缀，
        // 否则返回false.
        return f.getName().toLowerCase().toLowerCase().endsWith(".txt");
    }

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     *
     * @see
     */
    @Override
    public String getDescription() {
        return null;
    }
}
