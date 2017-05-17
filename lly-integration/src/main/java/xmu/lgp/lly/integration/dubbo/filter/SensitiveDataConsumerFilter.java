package xmu.lgp.lly.integration.dubbo.filter;

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
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.dubbo.rpc.RpcResult;

@Activate(group={"consumer"}, order=0)
public class SensitiveDataConsumerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveDataConsumerFilter.class);
    
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
        
        logger.debug("[ENRQ-BEGIN]敏感数据拦截器");
        try {
            if (getEncryptor().shouldClientDecryptRequest()) {
                logger.debug("敏感数据解密处理");
                if (invocation.getArguments() != null) {
                    Object originalArgs = SerializationUtils.clone(invocation.getArguments());
                    ((RpcInvocation)invocation).setArguments((Object[])originalArgs);
                    Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    ParamEncryptUtil.decrypt(method, invocation.getArguments(), getEncryptor());
                }
            }
        } catch (Exception e) {
            logger.error("[ENRQ-END]敏感数据处理异常", e);
            return new RpcResult(new SystemException(SystemErrorCodes.ENCRYPT_ERROR, e.getMessage(), e));
        }
        
        Result result = invoker.invoke(invocation);
        try {
            if (getEncryptor().shouldClientEncryptResponse()) {
                logger.debug("敏感数据返回加密处理");
                if (result.getValue() != null) {
                    Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    ((RpcResult)result).setValue(ParamEncryptUtil.encrypt(method, result.getValue(), getEncryptor()));
                }
            }
        } catch (Exception e) {
            logger.error("[ENRQ-END]敏感数据返回处理异常", e);
            return new RpcResult(new SystemException(SystemErrorCodes.ENCRYPT_ERROR, e.getMessage(), e));
        }
        logger.debug("[ENRQ-END]敏感数据拦截器");
        return result;
    }

}
