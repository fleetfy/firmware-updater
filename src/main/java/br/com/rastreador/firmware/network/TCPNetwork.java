package br.com.rastreador.firmware.network;

import br.com.rastreador.firmware.network.io.InputData;
import br.com.rastreador.firmware.network.io.OutputData;
import br.com.rastreador.firmware.network.io.ReadingThread;
import br.com.rastreador.firmware.network.io.WritingThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class TCPNetwork {

    private final InetAddress address;
    private final int port;
    private Socket socket;
    private final OnReceiveMessage onReceiveMessage;
    private WritingThread threadEscrita;
    private ReadingThread threadLeitura;

    /**
     * Método construtor da classe.
     *
     * @param address um {@link InetAddress}. Endereço do dispositivo.
     */
    public TCPNetwork(InetAddress address, int port, OnReceiveMessage onReceiveMessage) {
        this.address = address;
        this.port = port;
        this.onReceiveMessage = onReceiveMessage;
    }

    public synchronized void start() throws IOException {
        stop();
        socket = new Socket();
        socket.bind(new InetSocketAddress(address, port));
        TCPData tcpData = new TCPData(socket.getOutputStream(), socket.getInputStream());
        threadEscrita = new WritingThread(tcpData);
        threadEscrita.start();
        threadLeitura = new ReadingThread(tcpData, onReceiveMessage);
        threadLeitura.start();
    }

    /**
     * Método responsável finalizar a conexão ao dispositivo.
     */
    public synchronized void stop() throws IOException {
        if (threadEscrita != null) threadEscrita.stop();
        if (threadLeitura != null) threadLeitura.stop();
        if (socket != null) socket.close();
    }

    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }


    public InetAddress getAddress() {
        return address;
    }

    /**
     * Método responsável por adicionar um comando na fila de comandos a serem enviados ao dispositivo.
     *
     * @param message um {@link byte[]}. Mensagem a ser enviada.
     */
    public void enviarComando(byte[] message) {
        threadEscrita.send(message);
    }

    public static class TCPData implements OutputData, InputData {

        private final OutputStream outputStream;
        private final InputStream inputStream;

        public TCPData(OutputStream outputStream, InputStream inputStream) {
            this.outputStream = outputStream;
            this.inputStream = inputStream;
        }

        @Override
        public void write(byte[] message) {
            try {
                outputStream.write(message);
                outputStream.flush();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public byte[] read() {
            byte[] message = new byte[1024];
            int size = 0;

            try {
                size = inputStream.read(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Arrays.copyOf(message, size);
        }
    }

}