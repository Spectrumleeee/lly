package xmu.lgp.lly.integration.dubbo.filter;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xmu.lgp.lly.common.exception.SystemErrorCodes;
import xmu.lgp.lly.common.exception.SystemException;
import xmu.lgp.lly.common.security.FilterEncryptor;
import xmu.lgp.lly.common.util.ParamEncryptUtil;
import xmu.lgp.lly.framework.spring.SpringContextHolder;
import xmu.lgp.lly.integration.config.XmlServicesConfigurator;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;

@Activate(group={"provider"}, order=-6000)
public class SensitiveDataProviderFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveDataProviderFilter.class);
    
    private static FilterEncryptor filterEncryptor;
    
    private static Object lockObj = new Object();
    
    private FilterEncryptor getEncryptor() {
        if (filterEncryptor != null) {
            return filterEncryptor;
        }
        synchronized (lockObj) {
            if (filterEncryptor != null) {
                return filterEncryptor;
            }
            filterEncryptor = (FilterEncryptor)SpringContextHolder.getApplicationContext().getBean(FilterEncryptor.class);
        }
        return filterEncryptor;
    }
    
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!XmlServicesConfigurator.isSensitiveService(invoker.getInterface())) {
            return invoker.invoke(invocation);
        }
        logger.debug("[ENTX-BEGIN]敏感数据拦截器");
        try {
            if (getEncryptor().shouldServerEncryptRequest()) {
                logger.debug("敏感数据加密处理");
                Object[] args = invocation.getArguments();
                Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                ParamEncryptUtil.encrypt(method, args, getEncryptor());
                
            }
        } catch (Exception e) {
            logger.error("[ENTX-END]敏感数据处理异常", e);
            Throwable exception = new SystemException(SystemErrorCodes.ENCRYPT_ERROR, e.getMessage(), e);
            return new RpcResult(exception);
        }
        
        Result result = invoker.invoke(invocation);
        try {
            if (getEncryptor().shouldServerDecryptResponse()) {
                logger.debug("敏感数据返回解密处理");
                if (result.getValue() != null) {
                    Object orginalResult = SerializationUtils.clone((Serializable)result.getValue());
                    Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    // TODO blabla
                    ((RpcResult)result).setResult(orginalResult);
                }
            }
        } catch (Exception e) {
            
        }
        
        return null;
    }

    
}
