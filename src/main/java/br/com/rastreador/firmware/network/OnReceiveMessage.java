package br.com.rastreador.firmware.network;

public interface OnReceiveMessage {

    void receive(byte[] message);

}
