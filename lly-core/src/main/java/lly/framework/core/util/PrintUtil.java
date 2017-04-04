/**
 * Author:  liguangpu <spectrumleeee@gmail.com>
 * Created: 2017-4-4
 */

package lly.framework.core.util;

public class PrintUtil {
    
    public static void printClassBootstrapMsg(Class<?> clazz, String msg) {
        System.out.printf("%-15s >> %s\n", clazz.getSimpleName(), msg);
    }
    
}
