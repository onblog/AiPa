package cn.yueshutong;

import cn.yueshutong.executor.AiPaExecutor;
import cn.yueshutong.worker.AiPaWorker;

/**
 * Create by yster@foxmail.com 2018/9/27/027 14:38
 */
public class AiPa {

    public static AiPaExecutor newInstance(AiPaWorker aiPaWorker){
        return new AiPaExecutor(aiPaWorker);
    }

}
