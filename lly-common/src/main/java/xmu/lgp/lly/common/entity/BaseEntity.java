package xmu.lgp.lly.common.entity;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xmu.lgp.lly.common.annotation.Secret;

public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 8050108680140225191L;
    
    private static Map<Class<?>, PropertyDescriptor[]> propMap = new HashMap<>(256);
    
    private transient ThreadLocal<BaseEntity> visitor = new ThreadLocal<BaseEntity>() {
        protected BaseEntity initialValue() {
            return null;
        }
    };
    
    private String simpleName = getClass().getSimpleName();
    
    public String toString() {
        if (visitor.get() == null) {
            visitor.set(this);

            try {
                return toString0();
            } finally {
                visitor.set(null);
            }
        }
        return simpleName + "@" + Integer.toHexString(hashCode());
    }

    private String toString0() {
        try {
            PropertyDescriptor[] props = getProps();
            Object[] params = new Object[0];

            boolean isFirst = true;

            StringBuilder strBuilder = new StringBuilder(512);
            strBuilder.append(getClass().getName()).append("{");
            for (PropertyDescriptor descriptor : props) {
                Method m = descriptor.getReadMethod();
                if (m != null) {
                    boolean accessible = m.isAccessible();
                    if (!accessible) {
                        m.setAccessible(true);
                    }
                    try {
                        Object result = m.invoke(this, params);
                        if (isNullOrEmpty(result)) {
                            continue;
                        }

                        Class<?> cls = result.getClass();
                        boolean isArray = cls.isArray();
                        String arrayInfo = isArray ? toArrayString(cls.getComponentType(), result) : null;
                        if (isArray && arrayInfo == null) {
                            continue;
                        }
                        if (!isFirst) {
                            strBuilder.append(",");
                        } else {
                            isFirst = false;
                        }
                        strBuilder.append(descriptor.getName()).append(":");

                        if (result instanceof String) {
                            strBuilder.append('"').append(result).append('"');
                        } else if (isArray) {
                            strBuilder.append(arrayInfo);
                        } else {
                            strBuilder.append(result);
                        }
                    } catch (Exception e) {

                    }

                    if (!accessible) {
                        m.setAccessible(false);
                    }
                }
            }
            strBuilder.append('}');
            return strBuilder.toString();
        } catch (IntrospectionException e) {
            return super.toString();
        }
    }

    private String toArrayString(Class<?> comonentType, Object result) {
        String str;
        if (comonentType == Character.TYPE) {
            str = Arrays.toString((char[]) result);
        } else if (comonentType == Boolean.TYPE) {
            str = Arrays.toString((boolean[]) result);
        } else if (comonentType == Byte.TYPE) {
            str = Arrays.toString((byte[]) result);
        } else if (comonentType == Short.TYPE) {
            str = Arrays.toString((short[]) result);
        } else if (comonentType == Integer.TYPE) {
            str = Arrays.toString((int[]) result);
        } else if (comonentType == Long.TYPE) {
            str = Arrays.toString((long[]) result);
        } else if (comonentType == Float.TYPE) {
            str = Arrays.toString((float[]) result);
        } else if (comonentType == Double.TYPE) {
            str = Arrays.toString((double[]) result);
        } else {
            str = Arrays.toString((Object[]) result);
        }
        return (str == "[]" || str == "null") ? null : str;
    }

    private boolean isNullOrEmpty(Object result) {
        if (result == null) {
            return true;
        }
        if (result instanceof Map) {
            return ((Map<?, ?>) result).isEmpty();
        }
        if ((result instanceof List) || (result instanceof Collection)) {
            return ((Collection<?>) result).isEmpty();
        }
        return false;
    }

    protected PropertyDescriptor[] getProps() throws IntrospectionException {
        Class<?> clazz = getClass();
        PropertyDescriptor[] props = (PropertyDescriptor[]) propMap.get(getClass());

        if (props == null) {
            synchronized (propMap) {
                props = (PropertyDescriptor[]) propMap.get(clazz);
                if (props == null) {
                    PropertyDescriptor[] propArray = Introspector.getBeanInfo(clazz, Object.class)
                            .getPropertyDescriptors();
                    List<PropertyDescriptor> propList = new ArrayList<>(20);

                    for (PropertyDescriptor prop : propArray) {
                        Method m = prop.getReadMethod();
                        if (m != null) {
                            Secret secretAnnotation = (Secret) m.getAnnotation(Secret.class);
                            if (secretAnnotation == null) {
                                m = prop.getWriteMethod();
                                if (m != null) {
                                    propList.add(prop);
                                }
                            }
                        }
                    }
                    props = new PropertyDescriptor[propList.size()];
                    propList.toArray(props);

                    Arrays.sort(props, new Comparator<Object>() {
                        @Override
                        public int compare(Object param1, Object param2) {
                            int param1TypeSortValue = BaseEntity.getTypeSortValue(((PropertyDescriptor) param1)
                                    .getPropertyType());
                            int param2TypeSortValue = BaseEntity.getTypeSortValue(((PropertyDescriptor) param2)
                                    .getPropertyType());

                            return param1TypeSortValue - param2TypeSortValue;
                        }
                    });
                    propMap.put(clazz, props);
                }
            }
        }
        return props;
    }

    private static int getTypeSortValue(Class<?> clazz) {
        int result;
        if ((Map.class.isAssignableFrom(clazz)) || Collection.class.isAssignableFrom(clazz)) {
            result = 2;
        } else if (String.class == clazz || Number.class.isAssignableFrom(clazz) || clazz.isPrimitive()
                || clazz.isEnum()) {
            result = 0;
        } else {
            result = 1;
        }
        return result;
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
        
        visitor = new ThreadLocal<BaseEntity>() {
            
            protected BaseEntity initialValue() {
                return null;
            }
            
        };
    }
    
}
