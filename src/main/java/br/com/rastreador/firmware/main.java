package br.com.rastreador.firmware;

public class main {

    public static void main(String[] args) throws Throwable {
        new FirmwareUpdater("192.168.1.194",
                "/home/thiago/Documentos/projetos/github/rastreador/firmware/.pio/build/debug/firmware.bin");
    }

}
