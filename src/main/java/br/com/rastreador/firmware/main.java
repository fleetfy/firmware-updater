package br.com.rastreador.firmware;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class main {

    public static void main(String[] args) throws Throwable {
        AtomicBoolean atomicFinished = new AtomicBoolean(false);
        FirmwareUpdater updater = new FirmwareUpdater("192.168.1.187","./firmware.bin",
                new UpdateStatus() {
                    @Override
                    public void onSucess() {
                        System.out.println("Firmware update success!");
                        atomicFinished.set(true);
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("Firmware update error: " + error);
                        atomicFinished.set(true);
                    }
                });

        updater.initUpdate();

        new Thread(() -> {
            while (!atomicFinished.get()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
