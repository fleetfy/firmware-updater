package br.com.rastreador.firmware.network;

import br.com.rastreador.firmware.ArrayUtil;
import br.com.rastreador.firmware.ThreadPoolUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPNetworkServer {
    private final int PORT = 5050;
    private final OnReceiveMessage onReceiverMessage;
    private boolean running;

    private Thread serverThread;
    private byte[] bufferGeral;

    private ServerSocket server;

    public TCPNetworkServer(OnReceiveMessage onReceiveMessage) {
        this.onReceiverMessage = onReceiveMessage;
    }

    private void initServer() throws IOException {
        server = new ServerSocket();
        server.setReuseAddress(false);
        do {
            try {
                TimeUnit.SECONDS.sleep(1);
                server.bind(new InetSocketAddress(5050));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } while (!server.isBound());
    }

    public void start() {
        running = true;
        serverThread = new Thread(() -> {
            try {
                initServer();
                while (running) {
                    final Socket client = server.accept();
                    ThreadPoolUtil.submit(() -> {
                        try {
                            byte[] buffer = new byte[512];
                            int tamanhoLido;
                            try (InputStream in = client.getInputStream()) {
                                while (((tamanhoLido = in.read(buffer)) > 0)) {
                                    bufferGeral = ArrayUtil.concatenar(bufferGeral, Arrays.copyOfRange(buffer, 0, tamanhoLido));
                                    Arrays.fill(buffer, (byte) 0x00);
                                    onReceiverMessage.receive(bufferGeral);
                                }
                            }
                            client.close();
                        } catch (IOException ex) {
                            Logger.getLogger(TCPNetworkServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        serverThread.setName("TCPNetworkServer - Listener");
        serverThread.start();
    }

    public void stop() {
        running = false;
        if (serverThread != null) {
            serverThread.interrupt();
        }
        serverThread = null;
    }

    public int getPort() {
        return PORT;
    }

    public InetAddress getAddress() {
        return server.getInetAddress();
    }

}
