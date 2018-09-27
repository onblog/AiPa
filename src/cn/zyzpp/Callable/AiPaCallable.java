package cn.zyzpp.Callable;

import cn.zyzpp.worker.AiPaWorker;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Create by yster@foxmail.com 2018/9/26/026 23:10
 */
public class AiPaCallable implements Callable<Object> {
    private AiPaWorker aiPaWorker;// 用户实现的方法
    private int maxFailCount;// 最多失败次数
    private String link;    // 要爬取的网址
    private Charset charset; //网页编码
    private Map<String, String> header; //请求头
    private Connection.Method method;  //请求方法
    private int timeout; // 请求超时
    private String userAgent;

    public AiPaCallable() {
    }

    public AiPaCallable init(AiPaWorker aiPaWorker, int maxFailCount, String link, Charset charset, Map<String, String> header, Connection.Method method, int timeout, String userAgent) {
        this.aiPaWorker = aiPaWorker;
        this.maxFailCount = maxFailCount;
        this.link = link;
        this.charset = charset;
        this.header = header;
        this.method = method;
        this.timeout = timeout;
        this.userAgent = userAgent;
        return this;
    }

    @Override
    public Object call() {
        int c = 0;
        while (c < maxFailCount) {
            try {
                // 开始爬虫
                Document body = getHtmlDocument(link);
                // 执行任务
                return aiPaWorker.run(body);
            } catch (IOException e) {
                c++;
                if (c == maxFailCount) {
                    e.printStackTrace();
                }
            }
        }
        // 爬取失败执行的方法
        return aiPaWorker.fail(link);
    }

    /**
     * 默认爬虫方法
     *
     * @return Document
     * @throws IOException
     */
    protected Document getHtmlDocument(String link) throws IOException {
        // 爬虫开始运行
        Connection connection = Jsoup.connect(link).method(method);
        // 设置请求头
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                connection.header(entry.getKey(), entry.getValue());
            }
        }
        // 开爬
        Connection.Response response = connection.execute().charset(charset.name());
        // 转码
        return response.parse();
    }

}
