package xmu.lgp.lly.common.util;

// TODO
public class Base64 {
    
    private static final char[] intToBase64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final char[] intToAltBase64 = "!\"#$%&\\(),-.:;<>@[]^`_{|}~abcdefghijklmnopqrstuvwxyz0123456789+?".toCharArray();
    
    public static String byteArrayToBase64(byte[] a) {
        return byteArrayToBase64(a, false);
    }
    
    private static String byteArrayToBase64(byte[] a, boolean alternate) {
        int aLen = a.length;
        int numFullGroups = aLen / 3;
        int numBytesInPartialGroup = aLen - 3 * numFullGroups;
        int resultLen = 4 * ((aLen + 2) / 3);
        
        StringBuffer result = new StringBuffer(resultLen);
        char[] intToAlpha = alternate ? intToAltBase64 : intToBase64;
        
        int inCursor = 0;
        for (int i=0; i< numFullGroups; i++) {
            int byte0 = a[inCursor++] & 0xFF;
            int byte1 = a[inCursor++] & 0xFF;
            int byte2 = a[inCursor++] & 0xFF;
            result.append(intToAlpha[byte0 >> 2]);
            result.append(intToAlpha[(byte0 << 4 & 0x3F | byte1 >> 4)]);
            result.append(intToAlpha[(byte1 << 2 & 0x3F | byte2 >> 6)]);
            result.append(intToAlpha[(byte2 & 0x3F)]);
        }
        
        if (numBytesInPartialGroup != 0) {
            int byte0 = a[inCursor++] & 0xFF;
            result.append(intToAlpha[byte0 >> 2]);
        }
        return null;
    }
    
    public static void main(String[] args) {
        Base64.byteArrayToBase64("123".getBytes(), false);
    }
    
}
