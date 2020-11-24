package br.com.rastreador.firmware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtil {

    private static final ExecutorService threadPool;

    static {
        threadPool = Executors.newFixedThreadPool(5, new ThreadFactory() {
            private final AtomicInteger instanceCount = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                t.setName("FirwareUpdater_" + instanceCount.getAndIncrement());
                return t;
            }
        });
    }

    public static void submit(Runnable runnable) {
        threadPool.submit(runnable);
    }



}
