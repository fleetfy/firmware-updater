package br.com.rastreador.firmware.network;

import br.com.rastreador.firmware.ConversorUtil;
import br.com.rastreador.firmware.network.io.InputData;
import br.com.rastreador.firmware.network.io.OutputData;
import br.com.rastreador.firmware.network.io.ReadingThread;
import br.com.rastreador.firmware.network.io.WritingThread;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class UDPNetwork {

    private final InetAddress address;
    private final OnReceiveMessage onReceiveMessage;
    private DatagramSocket socket;
    private final int port;
    private int timeout = -1;

    private UDPData udpData;

    private WritingThread threadEscrita;
    private ReadingThread threadLeitura;

    public UDPNetwork(InetAddress address, int port, OnReceiveMessage onReceiveMessage) {
        this.port = port;
        this.address = address;
        this.onReceiveMessage = onReceiveMessage;
    }

    public synchronized void start() throws IOException  {
        stop();
        socket = new DatagramSocket();
        if (timeout > 0)
            socket.setSoTimeout(timeout);

        udpData = new UDPData(socket);
        threadEscrita = new WritingThread(udpData);
        threadEscrita.start();
        threadLeitura = new ReadingThread(udpData, onReceiveMessage);
        threadLeitura.start();
    }

    public synchronized void stop() {
        if (threadEscrita != null) threadEscrita.stop();
        if (threadLeitura != null) threadLeitura.stop();
        if (socket != null) socket.close();
    }

    public void setTimeout(int timeoutInMS) throws SocketException {
        if (timeoutInMS < 0) return;
        this.timeout = timeoutInMS;
        if (socket == null) return;
        socket.setSoTimeout(timeout);
    }

    public InetAddress getAddress() { return address; }

    public void sendMessage(byte[] message) {
        threadEscrita.send(message);
    }

    public void setReadSize(int size) {
        udpData.atomicSizeOfRead.set(size);
    }

    private class UDPData implements InputData, OutputData {

        private final DatagramSocket socket;

        private final AtomicInteger atomicSizeOfRead = new AtomicInteger(1024);

        public UDPData(DatagramSocket socket) {
            this.socket = socket;
        }

        @Override
        public byte[] read() {
            byte[] message = new byte[1024];
            byte[] messageRead = new byte[0];
            DatagramPacket packet = new DatagramPacket(message, message.length);
            try {
                socket.receive(packet);
                messageRead = Arrays.copyOf(packet.getData(), atomicSizeOfRead.get());
                System.out.println("UDPSocket read <<<-- " + ConversorUtil.bytesToHex(messageRead));
            } catch (SocketTimeoutException ignored) {
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return messageRead;
        }

        @Override
        public void write(byte[] message) {
            try {
                socket.send(new DatagramPacket(message, message.length, address, port));
                System.out.println("UDPSocket write -->>> " + ConversorUtil.bytesToHex(message));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


}
