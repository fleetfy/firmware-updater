package br.com.rastreador.firmware.network;

import br.com.rastreador.firmware.ConversorUtil;
import br.com.rastreador.firmware.network.io.InputData;
import br.com.rastreador.firmware.network.io.OutputData;
import br.com.rastreador.firmware.network.io.ReadingThread;
import br.com.rastreador.firmware.network.io.WritingThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPNetwork {

    private final InetAddress address;
    private final OnReceiveMessage onReceiveMessage;
    private DatagramSocket socket;
    private final int port;
    private int timeout = -1;

    private WritingThread threadEscrita;
    private ReadingThread threadLeitura;

    public UDPNetwork(InetAddress address, int port, OnReceiveMessage onReceiveMessage) throws SocketException {
        this.port = port;
        this.address = address;
        this.onReceiveMessage = onReceiveMessage;
    }

    public synchronized void start() throws IOException  {
        stop();
        socket = new DatagramSocket();
        if (timeout > 0)
            socket.setSoTimeout(timeout);

        UDPData udpData = new UDPData(socket);
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

        synchronized (socket) {
            socket.setSoTimeout(timeout);
        }
    }

    public void sendMessage(byte[] message) {
        threadEscrita.send(message);
    }

    private class UDPData implements InputData, OutputData {

        private final DatagramSocket socket;

        public UDPData(DatagramSocket socket) {
            this.socket = socket;
        }

        @Override
        public byte[] read() {
            byte[] message = new byte[1024];
            DatagramPacket packet = new DatagramPacket(message, message.length);
            try {
                socket.receive(packet);
                System.out.println("UDPSocket read <<<-- " + ConversorUtil.bytesToHex(packet.getData()));
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return packet.getData();
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
