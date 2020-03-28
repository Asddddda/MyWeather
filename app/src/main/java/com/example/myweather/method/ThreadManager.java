package com.example.myweather.method;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public ThreadManager(){ }

    public ExecutorService getExecutorService (){
        if (cachedThreadPool == null){
            synchronized (this){
                if(cachedThreadPool == null){
                    cachedThreadPool = Executors.newCachedThreadPool();
                }
            }
        }
        return cachedThreadPool;
    }
}
