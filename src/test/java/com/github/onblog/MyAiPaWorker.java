package com.github.onblog;

import com.github.onblog.executor.AiPaExecutor;
import com.github.onblog.util.AiPaUtil;
import com.github.onblog.worker.AiPaWorker;
import org.jsoup.nodes.Document;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Create by yster@foxmail.com 2019/3/13 0013 16:52
 */
public class MyAiPaWorker implements AiPaWorker {

    @Override
    public String run(Document doc, AiPaUtil util) {
        //使用JSOUP进行HTML解析获取想要的div节点和属性
        //保存在数据库或本地文件中
        //新增aiPaUtil工具类可以再次请求网址
        return doc.title();
    }

    @Override
    public Boolean fail(String link) {
        //任务执行失败
        //可以记录失败网址
        //记录日志
        return false;
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ExecutionException, InterruptedException {
        //准备网址集合
        List<String> linkList = new ArrayList<>();
        linkList.add("http://jb39.com/jibing/FeiQiZhong265988.htm");
        linkList.add("http://jb39.com/jibing/XiaoErGuoDu262953.htm");
        linkList.add("http://jb39.com/jibing/XinShengErShiFei250995.htm");
        linkList.add("http://jb39.com/jibing/GaoYuanFeiShuiZhong260310.htm");
        linkList.add("http://jb39.com/zhengzhuang/LuoYin337449.htm");
        //第一步：新建AiPa实例
        AiPaExecutor aiPaExecutor = AiPa.newInstance(new MyAiPaWorker()).setCharset(Charset.forName("GBK"));
        //第二步：提交任务
        for (int i = 0; i < 10; i++) {
            aiPaExecutor.submit(linkList);
        }
        //第三步：读取返回值
        List<Future> futureList = aiPaExecutor.getFutureList();
        for (int i = 0; i < futureList.size(); i++) {
            //get() 方法会阻塞当前线程直到获取返回值
            System.out.println(i+"："+futureList.get(i).get());
        }
        //第四步：关闭线程池
        aiPaExecutor.shutdown();
    }
}