package br.com.rastreador.firmware;

public class ConversorUtil {
    public static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length);
        byte[] var2 = bytes;
        int var3 = bytes.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte b = var2[var4];
            stringBuilder.append(String.format("%02X.", b));
        }

        return stringBuilder.toString();
    }

}
