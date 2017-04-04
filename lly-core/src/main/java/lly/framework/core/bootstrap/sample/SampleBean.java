/**
 * Author:  liguangpu <spectrumleeee@gmail.com>
 * Created: 2017-4-4
 */

package lly.framework.core.bootstrap.sample;

import lly.framework.core.util.PrintUtil;

import org.springframework.stereotype.Service;

/**
 * 使用注解@Service方式注入到Spring容器
 */
@Service
public class SampleBean {
    
    public SampleBean() {
        PrintUtil.printClassBootstrapMsg(this.getClass(), "构造成功");
    }
    
    public void init() {
        PrintUtil.printClassBootstrapMsg(this.getClass(), "init成功");
    }
    
    public void destry() {
        PrintUtil.printClassBootstrapMsg(this.getClass(), "destroy成功");
    }
}
