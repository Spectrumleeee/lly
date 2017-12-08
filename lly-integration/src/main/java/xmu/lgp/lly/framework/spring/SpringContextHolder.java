package xmu.lgp.lly.framework.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 在Spring配置文件中配置该类，Spring容器完成初始化后会把
 * 上下文对象通过这个类的setApplicationContext方法设置到这个类中
 * 
 * @author liguangpu
 * @date 2017-6-1 下午2:29:09
 */
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static volatile ApplicationContext applicationContext = null;

    public SpringContextHolder() {
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.applicationContext = null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

}
