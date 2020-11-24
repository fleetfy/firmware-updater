package br.com.rastreador.firmware.network.io;

import br.com.rastreador.firmware.ThreadPoolUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ThreadIO implements Runnable {
    protected final AtomicBoolean mRun = new AtomicBoolean(true);

    public boolean isRunning() {
        return mRun.get();
    }

    public void stop() {
        mRun.set(false);
    }

    public void start() {
        ThreadPoolUtil.submit(this);
    }

}