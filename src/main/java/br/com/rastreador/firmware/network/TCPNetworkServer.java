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
    private ServerSocket server;

    public TCPNetworkServer(OnReceiveMessage onReceiveMessage) {
        this.onReceiverMessage = onReceiveMessage;
    }

    public void initServer() throws IOException {
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

        System.out.println("TCPServer OK!");
    }

    public TCPNetwork waitingDevice() throws Throwable {
        server.setSoTimeout(10000);
        final Socket client = server.accept();
        System.out.println(client.isConnected());
        TCPNetwork tcpNetwork = new TCPNetwork(client, onReceiverMessage);
        tcpNetwork.start();

        return tcpNetwork;
    }

    public int getPort() {
        return PORT;
    }

}
