package xmu.lgp.lly.integration.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.helpers.FormattingTuple;

import xmu.lgp.lly.framework.spring.SpringContextHolder;

public class TrancatedMessageFormatter {
    
    static final char DELIM_START = '{';
    static final char DELIM_STOP = '}';
    static final String DELIM_STR = "{}";
    static final char ESCAPE_CHAR = '\\';
    static volatile TrancateTxLogConfiguration trancateTxLogConfiguration;
    
    private static TrancateTxLogConfiguration getTrancateTxLogConfiguration() {
        if (trancateTxLogConfiguration == null) {
            try {
                trancateTxLogConfiguration = (TrancateTxLogConfiguration)SpringContextHolder.getApplicationContext().getBean(TrancateTxLogConfiguration.class);
            } catch (Exception e) {
                trancateTxLogConfiguration = new TrancateTxLogConfiguration() {

                    @Override
                    public boolean isTrancateTxLog() {
                        return true;
                    }

                    @Override
                    public int getMaxTxLogLength() {
                        return 1024;
                    }
                };
            }
        }
        return trancateTxLogConfiguration;
    }
    
    public static final void logTXEND(Logger logger, String transmissionDeltaMillseconds, long invocationCostMills, String busiErrCode, String busiErrDesc, String sessionId, Object args, Object result) {
        if (logger.isInfoEnabled()) {
            FormattingTuple ft = formatTXEND(transmissionDeltaMillseconds, invocationCostMills, busiErrCode, busiErrDesc, sessionId, args, result);
            logger.info(ft.getMessage(), ft.getThrowable());
        }
    }
    
    public static final FormattingTuple formatTXEND(String transmissionDeltaMillseconds, long invocationCostMills, String busiErrCode, String busiErrDesc, String sessionId, Object args, Object result) {
        return arrayFormat("{}|{}|{}|{}|{}|[TX-END]|{}|{}", new Object[]{transmissionDeltaMillseconds, Long.valueOf(invocationCostMills), busiErrCode, busiErrDesc, sessionId, args, result}, getTrancateTxLogConfiguration().getMaxTxLogLength());
    }
    
    public static final FormattingTuple format(String messagePattern, Object arg, int maxLength) {
        return arrayFormat(messagePattern, new Object[]{arg}, maxLength);
    }
    
    public static final FormattingTuple format(String messagePattern, Object arg1, Object arg2, int maxLength) {
        return arrayFormat(messagePattern, new Object[]{arg1, arg2}, maxLength);
    }
    
    private static final Throwable getThrowableCandidate(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return null;
        }
        
        Object lastEntry = argArray[argArray.length - 1];
        if (lastEntry instanceof Throwable) {
            return (Throwable)lastEntry;
        }
        return null;
    }
    
    public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray, int maxLength) {
        Throwable throwableCandidate = getThrowableCandidate(argArray);
        
        if (messagePattern == null) {
            return new FormattingTuple(null, argArray, throwableCandidate);
        }
        
        if (argArray == null) {
            return new FormattingTuple(messagePattern);
        }
        
        int i = 0;
        StringBuffer sbf = new StringBuffer(messagePattern.length() + 50);
        int L = 0;
        for (L = 0; L < argArray.length; L++) {
            int j = messagePattern.indexOf("{}", i);
            if (j == -1) {
                if (i == 0) {
                    return new FormattingTuple(messagePattern, argArray, throwableCandidate);
                }
                
                sbf.append(messagePattern.substring(i, messagePattern.length()));
                trancateIfOverLength(sbf, maxLength);
                return new FormattingTuple(sbf.toString(), argArray, throwableCandidate);
            }
            
            if (isEscapedDelimeter(messagePattern, j)) {
                if (!isDoubleEscaped(messagePattern, j)) {
                    L--;
                    
                    sbf.append(messagePattern.substring(i, j - 1));
                    sbf.append("{");
                    i = j + 1;
                } else {
                    sbf.append(messagePattern.subSequence(i, j - 1));
                    deeplyAppendParameter(sbf, argArray[L], new HashSet(), maxLength);
                    i = j + 2;
                }
            } else {
                sbf.append(messagePattern.substring(i, j));
                deeplyAppendParameter(sbf, argArray[L], new HashSet(), maxLength);
                i = j + 2;
            }
        }
        
        sbf.append(messagePattern.substring(i, messagePattern.length()));
        trancateIfOverLength(sbf, maxLength);
        if (L < argArray.length - 1) {
            return new FormattingTuple(sbf.toString(), argArray, throwableCandidate);
        }
        return new FormattingTuple(sbf.toString(), argArray, null);
    }
    
    private static void trancateIfOverLength(StringBuffer sbf, int maxLength) {
        if (getTrancateTxLogConfiguration().isTrancateTxLog() && sbf.length() > maxLength) {
            sbf.delete(maxLength, sbf.length());
            sbf.append("...(trancated)");
        }
    }
    
    private static final boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex == 0) {
            return false;
        }
        
        char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        if (potentialEscape == '\\') {
            return true;
        }
        return false;
    }
    
    private static final boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == '\\') {
            return true;
        }
        return false;
    }
    
    private static void deeplyAppendParameter(StringBuffer sbf, Object o, Set<Object> seenMap, int maxLength) {
        if(getTrancateTxLogConfiguration().isTrancateTxLog() && sbf.length() >= maxLength) {
            return;
        }
        if (o == null) {
            sbf.append("null");
            return;
        }
        
        if (!o.getClass().isArray()) {
            if (getTrancateTxLogConfiguration().isTrancateTxLog() &&(o instanceof Collection)&& ((Collection)o).size() > 50) {
                sbf.append("[collection size is: " + ((Collection)o).size() + ", to much to print, omit...]");
            }else {
                safeObjectAppend(sbf, o);
            }
        } else if (o instanceof boolean[]) {
            booleanArrayAppend(sbf, (boolean[])o);
        } else if (o instanceof byte[]) {
            byteArrayAppend(sbf, (byte[])o);
        } else if (o instanceof char[]) {
            charArrayAppend(sbf, (char[])o);
        } else if (o instanceof short[]) {
            shortArrayAppend(sbf, (short[])o);
        } else if (o instanceof int[]) {
            intArrayAppend(sbf, (int[])o);
        } else if (o instanceof long[]) {
            longArrayAppend(sbf, (long[])o);
        } else if (o instanceof float[]) {
            floatArrayAppend(sbf, (float[])o);
        } else if (o instanceof double[]) {
            doubleArrayAppend(sbf, (double[])o);
        } else if (getTrancateTxLogConfiguration().isTrancateTxLog() && Array.getLength(o) > 50) {
            sbf.append("[array size is: " + ((Collection)o).size() + ", to much to print, omit...]");
        } else {
            objectArrayAppend(sbf, (Object[])o, seenMap, maxLength);
        }
    }
    
    private static void safeObjectAppend(StringBuffer sbf, Object o) {
        try {
            String oasString = o.toString();
            sbf.append(oasString);
        } catch (Exception e) {
            System.err.println("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]");
            
            e.printStackTrace();
            sbf.append("[FAILED toString()");
        }
    }
    
    private static void objectArrayAppend(StringBuffer sbf, Object[] a, Set<Object> seenMap, int maxLength) {
        sbf.append('[');
        if (!seenMap.contains(a)) {
            seenMap.add(a);
            int len = a.length;
            for (int i=0; i<len; i++) {
                deeplyAppendParameter(sbf, a[i], seenMap, maxLength);
                if (i != len -1) {
                    sbf.append(", ");
                }
            }
            seenMap.remove(a);
        } else {
            sbf.append("...");
        }
        sbf.append(']');
    }
    
    private static void booleanArrayAppend(StringBuffer sbf, boolean[] a) {
        sbf.append('[');
        int len = a.length;
        for (int i=0; i<len; i++) {
            sbf.append(a[i]);
            if (i != len -1) {
                sbf.append(", ");
            }
        }
        sbf.append(']');
    }
    
    private static void byteArrayAppend(StringBuffer sbf, byte[] a) {
        sbf.append('[');
        int len = a.length;
        for (int i=0; i<len; i++) {
            sbf.append(a[i]);
            if (i != len -1) {
                sbf.append(", ");
            }
        }
        sbf.append(']');
    }
    
    private static void charArrayAppend(StringBuffer sbf, char[] a) {
        sbf.append('[');
        int len = a.length;
        for (int i=0; i<len; i++) {
            sbf.append(a[i]);
            if (i != len -1) {
                sbf.append(", ");
            }
        }
        sbf.append(']');
    }
    
    private static void shortArrayAppend(StringBuffer sbf, short[] a) {
        sbf.append('[');
        int len = a.length;
        for (int i=0; i<len; i++) {
            sbf.append(a[i]);
            if (i != len -1) {
                sbf.append(", ");
            }
        }
        sbf.append(']');
    }
    
    private static void intArrayAppend(StringBuffer sbf, int[] a) {
        sbf.append('[');
        int len = a.length;
        for (int i=0; i<len; i++) {
            sbf.append(a[i]);
            if (i != len -1) {
                sbf.append(", ");
            }
        }
        sbf.append(']');
    }
    
    private static void longArrayAppend(StringBuffer sbf, long[] a) {
        sbf.append('[');
        int len = a.length;
        for (int i=0; i<len; i++) {
            sbf.append(a[i]);
            if (i != len -1) {
                sbf.append(", ");
            }
        }
        sbf.append(']');
    }
    
    private static void floatArrayAppend(StringBuffer sbf, float[] a) {
        sbf.append('[');
        int len = a.length;
        for (int i=0; i<len; i++) {
            sbf.append(a[i]);
            if (i != len -1) {
                sbf.append(", ");
            }
        }
        sbf.append(']');
    }
    
    private static void doubleArrayAppend(StringBuffer sbf, double[] a) {
        sbf.append('[');
        int len = a.length;
        for (int i=0; i<len; i++) {
            sbf.append(a[i]);
            if (i != len -1) {
                sbf.append(", ");
            }
        }
        sbf.append(']');
    }
    
}
