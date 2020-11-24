package br.com.rastreador.firmware;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ArrayUtil {
    public ArrayUtil() {
    }

    public static byte[] concatenar(byte[]... arrays) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int var3 = arrays.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte[] atual = arrays[var4];
            outputStream.write(atual);
        }

        return outputStream.toByteArray();
    }
}

