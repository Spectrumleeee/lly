package xmu.lgp.lly.common.orm;

import xmu.lgp.lly.common.exception.SystemErrorCodes;
import xmu.lgp.lly.common.exception.SystemException;
import xmu.lgp.lly.common.util.StringUtil;

public class StringValuedEnumReflect {

    private StringValuedEnumReflect() {
        throw new UnsupportedOperationException("This class must not be instanciated.");
    }
    
    private static <T extends Enum<T>> T[] getValues(Class<T> enumClass) {
        return (T[]) enumClass.getEnumConstants();
    }
    
    public static <T extends Enum<T>> String[] getStringValues(Class<T> enumClass) {
        T[] values = getValues(enumClass);
        String[] result = new String[values.length];
        for(int i=0; i<values.length; i++) {
            result[i] = ((StringValuedEnum)values[i]).getValue();
        }
        return result;
    }
    
    public static <T extends Enum<T>> String getNameFromValue(Class<T> enumClass, String value) {
        T[] values = getValues(enumClass);
        for(T val : values) {
            if (((StringValuedEnum)val).getValue().equals(value)) {
                return val.name();
            }
        }
        return "";
    }
    
    public static <T extends Enum<T>> T valueToEnum(Class<T> t, String value, boolean emptyEnable) {
        if (emptyEnable && StringUtil.isEmpty(value)) {
            return null;
        }
        
        String name = getNameFromValue(t, value);
        try {
            return Enum.valueOf(t, name);
        } catch (Exception e) {
            throw new SystemException(SystemErrorCodes.ILLEGAL_ENUM_VALUE, new Object[] {t.getName(), value, e});
        }
    }
    
}
