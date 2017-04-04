/**
 * Author:  liguangpu <spectrumleeee@gmail.com>
 * Created: 2017-4-4
 */

package lly.framework.core.bootstrap.sample;

import lly.framework.core.util.PrintUtil;

/**
 * 在Spring配置文件中用bean标签注入到Spring容器
 */
public class CommonBean {
    
    public CommonBean() {
        PrintUtil.printClassBootstrapMsg(this.getClass(), "构造成功");
    }
    
    public void init() {
        PrintUtil.printClassBootstrapMsg(this.getClass(), "init成功");
    }
    
    public void destroy() {
        PrintUtil.printClassBootstrapMsg(this.getClass(), "destroy成功");
    }
}
