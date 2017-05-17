package xmu.lgp.lly.common.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface Encrypt {
    
    NameValue[] context() default {};
    
}
