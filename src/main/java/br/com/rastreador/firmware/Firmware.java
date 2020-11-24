package br.com.rastreador.firmware;

import javax.xml.bind.DatatypeConverter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public class Firmware {

    private final byte[] firmware;
    private final String md5;

    public Firmware(Path file) throws Throwable {
        firmware = Files.readAllBytes(file);
        MessageDigest messageDigest = MessageDigest.getInstance("md5");
        md5 = DatatypeConverter.printHexBinary(messageDigest.digest(firmware)).toUpperCase();
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
}
