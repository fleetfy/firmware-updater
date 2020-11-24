package br.com.rastreador.firmware.network.io;

import br.com.rastreador.firmware.network.OnReceiveMessage;

public class ReadingThread extends ThreadIO {

    private final InputData inputData;
    private final OnReceiveMessage onReceiveMessage;

    public ReadingThread(InputData inputData, OnReceiveMessage onReceiveMessage) {
        this.inputData = inputData;
        this.onReceiveMessage = onReceiveMessage;
    }

    public void run() {
        byte[] message;
        try {
            while (isRunning() && (message = inputData.read()).length > 0) {
                if (!isRunning())
                    return;
                onReceiveMessage.receive(message);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
