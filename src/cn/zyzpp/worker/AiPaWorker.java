package cn.zyzpp.worker;


import org.jsoup.nodes.Document;

/**
 * 用户必须实现的接口
 * Create by yster@foxmail.com 2018/9/26/026 20:19
 */
public interface AiPaWorker<T,S> {
    /**
     * 如何解析爬下来的HTML文档？
     * @param doc JSOUP提供的文档
     * @return
     */
    T run(Document doc);

    /**
     * run方法异常则执行fail方法
     * @param link 网址
     * @return
     */
    S fail(String link);
}
