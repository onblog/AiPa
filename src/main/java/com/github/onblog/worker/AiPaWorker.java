package com.github.onblog.worker;


import com.github.onblog.util.AiPaUtil;
import org.jsoup.nodes.Document;

/**
 * 用户必须实现的接口
 * Create by yster@foxmail.com 2018/9/26/026 20:19
 */
public interface AiPaWorker<T,S> {
    /**
     * 如何解析爬下来的HTML文档？
     * @param doc JSOUP提供的文档
     * @param util 爬虫工具
     * @return
     */
    T run(Document doc, AiPaUtil util);

    /**
     * run方法异常则执行fail方法
     * @param link 网址
     * @return
     */
    S fail(String link);
}
