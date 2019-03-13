package cn.yueshutong.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Create by yster@foxmail.com 2018/9/27/027 20:26
 */
public class AiPaUtil {
    private Charset charset; //网页编码
    private Map<String, String> header; //请求头
    private Connection.Method method;  //请求方法
    private int timeout; // 请求超时
    private String userAgent;// UA
    private Map<String, String> cookies; //Cookies

    public AiPaUtil() {
    }

    public AiPaUtil(Charset charset, Map<String, String> header, Connection.Method method, int timeout, String userAgent, Map<String,String> cookies) {
        this.charset = charset;
        this.header = header;
        this.method = method;
        this.timeout = timeout;
        this.userAgent = userAgent;
        this.cookies = cookies;
    }

    /**
     * 默认爬虫方法
     *
     * @return Document
     * @throws IOException
     */
    public Document getHtmlDocument(String link) throws IOException {
        // 爬虫开始运行
        Connection connection = Jsoup.connect(link).method(method);
        // 设置请求头
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                connection.header(entry.getKey(), entry.getValue());
            }
        }
        //设置Cookies
        if (cookies != null) {
            connection.cookies(cookies);
        }
        // 开爬
        Connection.Response response = connection.execute().charset(charset.name());
        // 转码
        return response.parse();
    }
}
