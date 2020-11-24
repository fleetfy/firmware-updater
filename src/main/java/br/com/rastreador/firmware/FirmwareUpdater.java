package br.com.rastreador.firmware;

import br.com.rastreador.firmware.network.OnReceiveMessage;
import br.com.rastreador.firmware.network.TCPNetworkServer;
import br.com.rastreador.firmware.network.UDPNetwork;

import java.net.InetAddress;
import java.nio.file.Paths;

public class FirmwareUpdater {

    private final TCPNetworkServer networkServer;
    private final UDPNetwork udpNetwork;
    private final Firmware firmware;

    private final int FLASH_COMMAND = 0;

    public FirmwareUpdater(String address, String filePath) throws Throwable {
        networkServer = new TCPNetworkServer(onServerReceiveMessage());
        networkServer.start();

        firmware = new Firmware(Paths.get(filePath));

        udpNetwork = new UDPNetwork(InetAddress.getByName(address), 3232, onUDPNetworkReceiveMessage());
        udpNetwork.setTimeout(10000);
        udpNetwork.start();

        initUpdate();
    }

    private void initUpdate() {
        String message = String.format("%d %d %d %s\n", FLASH_COMMAND,
                networkServer.getPort(), firmware.getSize(), firmware.getMd5());

        System.out.println("Invitation Message -> " + message);
        udpNetwork.sendMessage(message.getBytes());
    }

    public OnReceiveMessage onServerReceiveMessage() {
        return (message) -> {

        };
    }

    public OnReceiveMessage onUDPNetworkReceiveMessage() {
        return (message) -> {

        };
    }


}
