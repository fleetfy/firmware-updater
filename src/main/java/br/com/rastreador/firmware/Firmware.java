package br.com.rastreador.firmware;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public class Firmware {

    private final byte[] firmware;
    private final String md5;
    private final String fileName;

    public Firmware(Path file) throws Throwable {
        fileName = file.toString();
        firmware = Files.readAllBytes(file);
        MessageDigest messageDigest = MessageDigest.getInstance("md5");
        md5 = ConversorUtil.bytesToHex(messageDigest.digest(firmware)).toLowerCase();
    }

    public String getMd5() {
        return md5;
    }

    public int getSize() {
        return firmware.length;
    }

    public byte[] getFile() {
        return firmware;
    }

    public String getFileName() {
        return fileName;
    }

}
