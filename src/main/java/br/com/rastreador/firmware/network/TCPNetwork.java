package br.com.rastreador.firmware.network;

import br.com.rastreador.firmware.ConversorUtil;
import br.com.rastreador.firmware.network.io.InputData;
import br.com.rastreador.firmware.network.io.OutputData;
import br.com.rastreador.firmware.network.io.ReadingThread;
import br.com.rastreador.firmware.network.io.WritingThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class TCPNetwork {

    private final Socket socket;
    private final OnReceiveMessage onReceiveMessage;
    private WritingThread threadEscrita;
    private ReadingThread threadLeitura;

    public TCPNetwork(Socket socket, OnReceiveMessage onReceiveMessage) {
        this.socket = socket;
        this.onReceiveMessage = onReceiveMessage;
    }

    public synchronized void start() throws IOException {
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
                System.out.println("TCPSocket write -->>> " + ConversorUtil.bytesToHex(message));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public byte[] read() {
            byte[] message = new byte[1024];
            byte[] messageRead = new byte[0];
            int size;

            try {
                size = inputStream.read(message);
                if (size < 0) return new byte[0];
                messageRead = Arrays.copyOf(message, size);
                System.out.println("TCPSocket read <<<-- " + ConversorUtil.bytesToHex(messageRead));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return messageRead;
        }
    }

}