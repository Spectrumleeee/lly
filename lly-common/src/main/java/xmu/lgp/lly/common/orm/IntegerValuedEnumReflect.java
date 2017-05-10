package xmu.lgp.lly.common.orm;

import xmu.lgp.lly.common.exception.SystemErrorCodes;
import xmu.lgp.lly.common.exception.SystemException;

public class IntegerValuedEnumReflect {
    
    private IntegerValuedEnumReflect() {
        throw new UnsupportedOperationException("This class must not be instanciated.");
    }
    
    private static <T extends Enum<T>> T[] getValues(Class<T> enumClass) {
        return (T[])enumClass.getEnumConstants();
    }
    
    public static <T extends Enum<T>> int[] getStringValues(Class<T> enumClass) {
        T[] values = getValues(enumClass);
        int[] result = new int[values.length];
        for(int i=0; i<values.length; i++) {
            result[i] = ((IntegerValuedEnum)values[i]).getValue();
        }
        return result;
    }
    
    public static <T extends Enum<T>> String getNameFromValue(Class<T> enumClass, int value) {
        T[] values = getValues(enumClass);
        for (int i=0; i<values.length; i++) {
            if(((IntegerValuedEnum)values[i]).getValue() == value) {
                return values[i].name();
            }
        }
        return "";
    }
    
    public static <T extends Enum<T>> T valueToEnum(Class<T> t, int value, boolean emptyEnable) {
        if (emptyEnable && value < 0) {
            return null;
        }
        
        String name = getNameFromValue(t, value);
        try {
            return Enum.valueOf(t, name);
        } catch (Exception e) {
            throw new SystemException(SystemErrorCodes.ILLEGAL_ENUM_VALUE, new Object[] {t.getName(), Integer.valueOf(value), e});
        }
    }
}
