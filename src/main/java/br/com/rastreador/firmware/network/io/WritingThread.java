package br.com.rastreador.firmware.network.io;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WritingThread extends ThreadIO {

    private final LinkedBlockingQueue<byte[]> messagesToSend;
    private final OutputData output;

    public WritingThread(OutputData output) {
        this.messagesToSend = new LinkedBlockingQueue<>();
        this.output = output;
    }

    public void run() {
        try {
            while (isRunning()) {
                send();
                TimeUnit.MILLISECONDS.sleep(10);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void send() {
        while (messagesToSend.size() > 0) {
            byte[] message = messagesToSend.poll();
            Objects.requireNonNull(message);
            output.write(message);
        }
    }

    public void send(byte[] message) {
        if (message == null) return;
        messagesToSend.add(message);
    }

}
