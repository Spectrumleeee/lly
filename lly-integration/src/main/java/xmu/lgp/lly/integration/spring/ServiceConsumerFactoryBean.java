package xmu.lgp.lly.integration.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import xmu.lgp.lly.integration.config.ServiceConfig;

@Deprecated
public class ServiceConsumerFactoryBean implements ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext appCtx;
    private ServiceConfig serviceConfig;
    private String protocol;

    public void onApplicationEvent(ContextRefreshedEvent event) {}

    public ApplicationContext getAppCtx() {
        return appCtx;
    }

    public void setAppCtx(ApplicationContext appCtx) {
        this.appCtx = appCtx;
    }

    public ServiceConfig getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
