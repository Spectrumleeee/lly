package xmu.lgp.lly.integration.dubbo.filter;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;

import xmu.lgp.lly.common.context.ContextSlf4jUtil;
import xmu.lgp.lly.common.context.ServiceContext;
import xmu.lgp.lly.common.exception.BusinessException;
import xmu.lgp.lly.common.exception.BusinessTimeoutException;
import xmu.lgp.lly.common.exception.DatabaseErrorCodes;
import xmu.lgp.lly.common.exception.DatabaseException;
import xmu.lgp.lly.common.exception.ServiceException;
import xmu.lgp.lly.common.exception.SystemErrorCodes;
import xmu.lgp.lly.common.exception.SystemException;
import xmu.lgp.lly.common.util.DateUtil;
import xmu.lgp.lly.common.util.StringUtil;
import xmu.lgp.lly.integration.util.TrancatedMessageFormatter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
import com.alibaba.dubbo.rpc.service.GenericService;

@Activate(group={"provider"})
public class ExceptionFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionFilter.class);
    private static final AtomicInteger processingRequestCounter = new AtomicInteger(0);
    
    public ExceptionFilter() {
        logger.info("创建ExceptionFilter");
    }
    
    public static int getProcessingRequestCounter() {
        return processingRequestCounter.get();
    }
    
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        processingRequestCounter.incrementAndGet();
        long startTime = System.currentTimeMillis();
        ServiceContext context = ServiceContext.getContext();
        String sessionId = context.getSessionId() == null ? "" : context.getSessionId();
        String transimissionDeltaMillseconds = "NA";
        if (!StringUtil.isEmpty(context.getRequestDateTime())) {
            try {
                transimissionDeltaMillseconds = String.valueOf(startTime - DateUtil.parse(context.getRequestDateTime(), "yyyy-MM-dd HH:mm:ss.SSS").getTime());
            } catch (ParseException e) {
                logger.error("解析请求时间异常", e);
            }
        }
        
        String methodName = invocation.getMethodName();
        Class<?> interfaceCls = invoker.getInterface();
        logger.info("{}|[TX-BEGIN][{}.{}][{}]", new Object[]{transimissionDeltaMillseconds, interfaceCls.getName(), methodName, sessionId});
        
        String sysErrCode = "SUCCESS";
        String busiErrCode = "SUCCESS";
        String busiErrDesc = "NA";
        Result result = null;
        try {
            result = invoker.invoke(invocation);
            if (result.hasException() && GenericService.class != interfaceCls) {
                busiErrCode = "ERROR";
                try {
                    Throwable exception = result.getException();
                    if (exception instanceof RuntimeException && exception instanceof Exception) {
                        sysErrCode = "6";
                        return result;
                    }
                    
                    Method method = interfaceCls.getMethod(methodName, invocation.getParameterTypes());
                    if (exception instanceof BusinessException) {
                        String msg = exception.getMessage();
                        String lineNumber = "-";
                        if (!logger.isDebugEnabled()) {
                            lineNumber = String.valueOf(exception.getStackTrace()[0].getLineNumber());
                        }
                        logger.info("{}.{}():{}调用异常：{}", new Object[]{method.getDeclaringClass().getName(), method.getName(), lineNumber, msg});
                        logger.debug("异常栈信息：", exception);
                    } else {
                        logger.error(method.getDeclaringClass().getName() + "." + method.getName() + "() 调用异常:", exception);
                    }
                    if (exception instanceof ServiceException) {
                        sysErrCode = "1";
                        busiErrCode = ((ServiceException)exception).getCode();
                        busiErrDesc = ((ServiceException)exception).getMessage();
                        if (busiErrCode == null) {
                            busiErrCode = "ERROR";
                        }
                        return result;
                    }
                    if (exception instanceof DataIntegrityViolationException) {
                        sysErrCode = "4";
                        exception = new DatabaseException(DatabaseErrorCodes.DUPLICATE_KEY, new Object[]{exception.getMessage()});
                    } else if (exception instanceof QueryTimeoutException){
                        sysErrCode = "4";
                        exception = new DatabaseException(DatabaseErrorCodes.CONNECTION_TIMEOUT);
                    } else if (exception instanceof DataAccessResourceFailureException) {
                        sysErrCode = "4";
                        exception = new DatabaseException(DatabaseErrorCodes.CANT_GET_CONNECTION);
                    } else if (exception instanceof DataAccessException) {
                        sysErrCode = "4";
                        exception = new DatabaseException(DatabaseErrorCodes.UNKNOWN_ERROR);
                    } else {
                        sysErrCode = "6";
                        exception = new SystemException(SystemErrorCodes.SYSTEM_UNKOWN_ERROR, new Object[]{exception.getMessage()});
                    }
                    
                    logger.info("框架统一补货转换异常：" + exception.getMessage());
                    return new RpcResult(exception);
                } catch (Exception e) {
                    sysErrCode = "6";
                    StringBuilder errMsg = new StringBuilder(200);
                    errMsg.append("Fail to ExceptionFilter when called by ")
                          .append(RpcContext.getContext().getRemoteHost())
                          .append(". service:")
                          .append(interfaceCls.getName())
                          .append(", method: ")
                          .append(methodName)
                          .append(", exception: ")
                          .append(e.getClass().getName())
                          .append(": ")
                          .append(e.getMessage());
                    logger.warn(errMsg.toString(), e);
                    return result;
                }
            }
            return result;
        } catch (Exception e) {
            sysErrCode = "6";
            StringBuilder errMsg = new StringBuilder(200);
            errMsg.append("Got RuntimeException which called by ")
                  .append(RpcContext.getContext().getRemoteHost())
                  .append(". service:")
                  .append(interfaceCls.getName())
                  .append(", method: ")
                  .append(methodName)
                  .append(", exception: ")
                  .append(e.getClass().getName())
                  .append(": ")
                  .append(e.getMessage());
            logger.error(errMsg.toString(), e);
            if (e instanceof RpcException) {
                throw new BusinessTimeoutException(sysErrCode, errMsg.toString(), e);
            }
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            
            if (null != interfaceCls) {
                context.setInvocationInterface(interfaceCls.getName());
            } else {
                context.setInvocationInterface("UNKNOWN");
            }
            context.setInvocationMethod(methodName);
            ContextSlf4jUtil.addLogKey2MDC(context);
            if (result != null) {
                TrancatedMessageFormatter.logTXEND(logger, transimissionDeltaMillseconds, endTime - startTime, busiErrCode, busiErrDesc, sessionId, invocation.getArguments(), result.getValue());
            }
            
            processingRequestCounter.decrementAndGet();
        }
    }
    
    
}
