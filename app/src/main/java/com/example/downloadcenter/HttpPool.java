package com.example.downloadcenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class HttpPool {
    private volatile static HttpPool httpPool = null;
    private volatile static ExecutorService cachePool = null;
    private volatile static ExecutorService fixedPool = null;
    private volatile static ScheduledExecutorService schedulePool = null;
    private volatile static ExecutorService singlePool = null;

    private HttpPool(){}

    public static HttpPool getInstance() {
        if (httpPool == null) {
            synchronized (HttpPool.class) {
                if (httpPool == null) {
                    httpPool = new HttpPool();
                }
            }
        }
        return httpPool;
    }

    public ExecutorService getCachePool() {
        if (cachePool == null) {
            synchronized (this) {
                if (cachePool == null) {
                    cachePool = Executors.newCachedThreadPool();
                }
            }
        }
        return cachePool;
    }

    public ExecutorService getFixedPool(int mThreads) {
        if (fixedPool == null) {
        synchronized (this) {
            if (fixedPool == null) {
                fixedPool = Executors.newFixedThreadPool(mThreads);
            }
        }
    }
    return fixedPool;
    }

    public ScheduledExecutorService getSchedulePool(int mThreads) {
        if (schedulePool == null) {
            synchronized (this) {
                if (schedulePool == null) {
                    schedulePool = Executors.newScheduledThreadPool(mThreads);
                }
            }
        }
        return schedulePool;
    }

    public ExecutorService getSinglePool() {
        if (singlePool == null) {
            synchronized (this) {
                if (singlePool == null) {
                    singlePool = Executors.newSingleThreadExecutor();
                }
            }
        }
        return singlePool;
    }

}

