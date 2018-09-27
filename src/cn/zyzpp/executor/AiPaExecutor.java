package cn.zyzpp.executor;

import cn.zyzpp.Callable.AiPaCallable;
import cn.zyzpp.util.AiPaUtil;
import cn.zyzpp.worker.AiPaWorker;
import org.jsoup.Connection;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Create by yster@foxmail.com 2018/9/26/026 22:52
 */
public class AiPaExecutor {
    private AiPaWorker aiPaWorker;
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    private int maxFailCount = 5;
    private Charset charset = Charset.forName("UTF-8");
    private Map<String, String> header = null;
    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";
    private int timeout = 30 * 1000;
    private Connection.Method method = Connection.Method.GET;
    private List<Future> futureList = new Vector<>();

    /**
     * @param aiPaWorker 用户必须实现的接口
     */
    public AiPaExecutor(AiPaWorker aiPaWorker) {
        this.aiPaWorker = aiPaWorker;
    }

    /**
     * 提交任务
     *
     * @param list List集合
     */
    public void submit(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            futureList.add(executor.submit(new AiPaCallable().init(aiPaWorker, maxFailCount, list.get(i), charset, header, method, timeout, userAgent)));
        }
    }

    /**
     * 提交任务
     *
     * @param list
     * @param aiPaUtil  重写爬虫代码则
     */
    public void submit(List<String> list, Class<? extends AiPaUtil> aiPaUtil) throws IllegalAccessException, InstantiationException {
        for (int i = 0; i < list.size(); i++) {
            futureList.add(executor.submit(new AiPaCallable().init(aiPaWorker, maxFailCount, list.get(i),aiPaUtil.newInstance())));
        }
    }

    /**
     * 执行完后关闭线程池
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * 返回任务执行结果
     *
     * @return
     */
    public List<Future> getFutureList() {
        return futureList;
    }

    /**
     * 返回线程池
     *
     * @return
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * @param threads 工作线程数量
     * @return
     */
    public AiPaExecutor setThreads(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
        return this;
    }

    /**
     * 重写爬虫代码则属性无效
     * @param charset 网页解码格式
     * @return
     */
    public AiPaExecutor setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * @param maxFailCount 爬取失败最多尝试次数
     * @return
     */
    public AiPaExecutor setMaxFailCount(int maxFailCount) {
        this.maxFailCount = maxFailCount;
        return this;
    }

    /**
     * 重写爬虫代码则属性无效
     * @param header 请求头
     */
    public AiPaExecutor setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    /**
     * 重写爬虫代码则属性无效
     * @param method 请求方法
     */
    public AiPaExecutor setMethod(Connection.Method method) {
        this.method = method;
        return this;
    }

    /**
     * 默认超时时间为30秒(30000毫秒)。零超时被视为无限超时。
     * 重写爬虫代码则属性无效
     * @param timeout 请求超时
     */
    public AiPaExecutor setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 自定义UA
     * 重写爬虫代码则属性无效
     * @param userAgent
     */
    public AiPaExecutor setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

}
