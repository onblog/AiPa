package cn.zyzpp;

import cn.zyzpp.util.AiPaUtil;
import cn.zyzpp.worker.AiPaWorker;
import org.jsoup.nodes.Document;

/**
 * Create by yster@foxmail.com 2018/9/27/027 14:36
 */
public class MyAiPaWorker implements AiPaWorker {

    @Override
    public String run(Document doc, AiPaUtil util) {
        //使用JSOUP进行HTML解析
        return doc.title() + doc.body().text();
    }

    @Override
    public Boolean fail(String link) {
        //任务执行失败
        //可以记录失败网址
        //记录日志
        return false;
    }
}
