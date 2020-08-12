package com.github.onblog;

import com.github.onblog.executor.AiPaExecutor;
import com.github.onblog.worker.AiPaWorker;

/**
 * Create by yster@foxmail.com 2018/9/27/027 14:38
 */
public class AiPa {

    public static AiPaExecutor newInstance(AiPaWorker aiPaWorker){
        return new AiPaExecutor(aiPaWorker);
    }

}
