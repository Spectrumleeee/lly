package xmu.lgp.lly.common.util;

import java.math.BigDecimal;

public class MathUtil {
    
    public static final int SCALE = 2;
    public static final int ROUND_MOD = 4;
    
    public static BigDecimal roundTwoScale(BigDecimal srcDecimal) {
        if (srcDecimal == null) {
            return BigDecimal.ZERO;
        }
        return srcDecimal.setScale(2, 4);
    }
    
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        assert(divisor != null && !eq(divisor, BigDecimal.ZERO));
        if (dividend == null) {
            return BigDecimal.ZERO;
        }
        return dividend.divide(divisor, 2, 4);
    }
    
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
        assert(scale > 0 && scale <=8);
        assert(divisor != null && !eq(divisor, BigDecimal.ZERO));
        if (dividend == null) {
            return BigDecimal.ZERO;
        }
        return dividend.divide(divisor, scale, 4);
    }
    
    public static boolean eq(BigDecimal decimal1, BigDecimal decimal2) {
        return compare(decimal1, decimal2) == 0;
    }
    
    public static boolean lt(BigDecimal decimal1, BigDecimal decimal2) {
        return !ge(decimal1, decimal2);
    }
    
    public static boolean le(BigDecimal decimal1, BigDecimal decimal2) {
        return !gt(decimal1, decimal2);
    }
    
    public static boolean gt(BigDecimal decimal1, BigDecimal decimal2) {
        return compare(decimal1, decimal2) > 0;
    }
    
    public static boolean ge(BigDecimal decimal1, BigDecimal decimal2) {
        return compare(decimal1, decimal2) >=0;
    }
    
    public static int compare(BigDecimal first, BigDecimal second) {
        if(first == null) {
            return second == null ? 0 : -1;
        }
        
        if(second == null) {
            return first == null ? 0 : 1;
        }
        
        if (!BigDecimal.ZERO.equals(second)) {
            return first.subtract(second).setScale(2, 4).compareTo(BigDecimal.ZERO);
        }
        
        return first.setScale(2, 4).compareTo(BigDecimal.ZERO);
    }
    
    
}
