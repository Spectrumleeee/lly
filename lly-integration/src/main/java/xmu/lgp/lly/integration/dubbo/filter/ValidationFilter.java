package xmu.lgp.lly.integration.dubbo.filter;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import xmu.lgp.lly.common.exception.ErrorMessageResource;
import xmu.lgp.lly.common.exception.SystemErrorCodes;
import xmu.lgp.lly.common.exception.SystemException;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
import com.alibaba.dubbo.validation.Validation;
import com.alibaba.dubbo.validation.Validator;

@Activate(group={"provider"}, value={"validation"}, after={"exception"})
public class ValidationFilter implements Filter {

    private Validation validation;
    
    public void setValidation(Validation validation) {
        this.validation = validation;
    }
    
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (validation != null && !invocation.getMethodName().startsWith("$") 
                && ConfigUtils.isNotEmpty(invoker.getUrl().getMethodParameter(invocation.getMethodName(), "validation"))) {
            try {
                Validator validator = validation.getValidator(invoker.getUrl());
                if (validator != null) {
                    validator.validate(invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
                }
            } catch (ConstraintViolationException e) {
                Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
                for (ConstraintViolation<?> violation : violations) {
                    String template = violation.getMessageTemplate();
                    if (template != null && template.equals(violation.getMessage())) {
                        String msg = ErrorMessageResource.getInstance().getMessage(template);
                        return new RpcResult(new SystemException(SystemErrorCodes.INVALID_PARAM_VALUE2, new Object[]{msg, template}));
                    }
                    return new RpcResult(new SystemException(SystemErrorCodes.INVALID_PARAM_VALUE, new Object[]{violation.getRootBeanClass().getName(), violation.getPropertyPath(), violation.getMessage(), e}));
                }
            } catch (RpcException e) {
                throw e;
            } catch (Exception t) {
                throw new RpcException(t.getMessage(), t);
            }
        }
        return invoker.invoke(invocation);
    }
}
