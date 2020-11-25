package br.com.rastreador.firmware;

import br.com.rastreador.firmware.network.OnReceiveMessage;
import br.com.rastreador.firmware.network.TCPNetworkServer;
import br.com.rastreador.firmware.network.UDPNetwork;

import java.net.InetAddress;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FirmwareUpdater {

    private final TCPNetworkServer networkServer;
    private final UDPNetwork udpNetwork;
    private final Firmware firmware;

    private final UpdateStatus updateStatus;

    private final int FLASH_COMMAND = 0;
    private final int AUTH_COMMAND = 200;

    public FirmwareUpdater(String address, String filePath, UpdateStatus updateStatus) throws Throwable {
        networkServer = new TCPNetworkServer(onServerReceiveMessage());
        networkServer.start();

        this.updateStatus = updateStatus;

        firmware = new Firmware(Paths.get(filePath));

        udpNetwork = new UDPNetwork(InetAddress.getByName(address), 3232, onUDPNetworkReceiveMessage());
        udpNetwork.setTimeout(10000);
        udpNetwork.start();
    }

    public void initUpdate() {
        String message = String.format("%d %d %d %s\n", FLASH_COMMAND,
                networkServer.getPort(), firmware.getSize(), firmware.getMd5());

        System.out.println("Invitation Message -> " + message);
        udpNetwork.sendMessage(message.getBytes());
    }

    private void authenticate(String data) throws Throwable {
        if (data == null || data.isEmpty()) return;
        String nonce = data.split("")[1];
        String cnonceText = String.format("%s%d%s%s", firmware.getFileName(), firmware.getSize(),
                firmware.getMd5(), udpNetwork.getAddress().getHostAddress());

        MessageDigest messageDigest = MessageDigest.getInstance("md5");
        String cnonce = ConversorUtil.bytesToHex(messageDigest.digest(cnonceText.getBytes())).toLowerCase();
        String passMD5 = ConversorUtil.bytesToHex(messageDigest.digest("dac5f6fe0584ba086744fd7d565caf14".getBytes())).toLowerCase();

        String resultText = String.format("%s:%s:%s", passMD5, nonce, cnonce);
        String result = ConversorUtil.bytesToHex(messageDigest.digest(resultText.getBytes()));

        System.out.println("Authenticating...");

        String message = String.format("%d %s %s\n", AUTH_COMMAND, cnonce, result);
        udpNetwork.sendMessage(message.getBytes());
    }

    private OnReceiveMessage onServerReceiveMessage() {
        return (message) -> {

        };
    }

    private OnReceiveMessage onUDPNetworkReceiveMessage() {
        return (message) -> {
            String data = new String(message).trim();
            if (!data.equals("OK")) {
                if (data.startsWith("AUTH")) {
                    try {
                        authenticate(data);
                        return;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }

                udpNetwork.stop();
                updateStatus.onError(data);
                return;
            }

            udpNetwork.stop();
            //wait esp connect on the TCPSocket
        };
    }


}
