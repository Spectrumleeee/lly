/**
 * Author:  liguangpu <spectrumleeee@gmail.com>
 * Created: 2017-4-4
 */

package lly.framework.core.util;

import org.junit.Test;

public class PrintUtilTest {

    @Test
    public void testPrintClassBootstrapMsg() {
        PrintUtil.printClassBootstrapMsg(this.getClass(), "测试");
    }

}
