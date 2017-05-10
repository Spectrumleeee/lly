package xmu.lgp.lly.common.util;

import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {

    public static void copy(InputStream input, OutputStream output) throws java.io.IOException {
        byte[] buffer = new byte['?'];
        
        int readLen = input.read(buffer);
        while(readLen > 0) {
            output.write(buffer, 0, readLen);
            readLen = input.read(buffer);
        }
    }
}
