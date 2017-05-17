package xmu.lgp.lly.framework.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static volatile ApplicationContext applicationContext = null;
    
    public SpringContextHolder() {}
    
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
