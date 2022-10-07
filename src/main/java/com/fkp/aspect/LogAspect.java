package com.fkp.aspect;

import com.fkp.annotation.LogPointCut;
import com.fkp.constant.GlobalConstant;
import com.fkp.constant.ResponseCodeConstant;
import com.fkp.domain.OperationLog;
import com.fkp.param.BaseResponse;
import com.fkp.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private LogService logService;

    //扫描所有方法带有LogPointCut注解的方法为切点
    @Pointcut("@within(logAnnotation) || @annotation(logAnnotation)")
    public void log(LogPointCut logAnnotation){
    }

    //定义切面
    @Around(value = "log(logAnnotation)", argNames = "joinPoint,logAnnotation")
    public Object run(ProceedingJoinPoint joinPoint, LogPointCut logAnnotation) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Object[] args = joinPoint.getArgs();
        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            String uri = request.getRequestURI();
            String token = request.getHeader(GlobalConstant.TOKEN);
            String operationCode = joinPoint.getSignature().getName();
            String operationDesc = joinPoint.getSignature().getName();
            if(logAnnotation != null){
                operationCode = logAnnotation.operationCode();
                operationDesc = logAnnotation.operationDesc();
            }
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationCode(operationCode);
            operationLog.setOperationDesc(operationDesc);
            operationLog.setToken(token);
            operationLog.setUri(uri);
            BaseResponse<?> response = (BaseResponse<?>) joinPoint.proceed(args);
            if(ResponseCodeConstant.STATUS_SUCCESS.equals(response.getStatus())){
                operationLog.setResult(GlobalConstant.OPERATION_LOG_RESULT_SUCCESS);
            }else {
                operationLog.setResult(GlobalConstant.OPERATION_LOG_RESULT_FAIL);
            }
            log.debug("request: uri={}, method={}, args={}",uri, request.getMethod(),args);
            log.debug("response: {}",response);
            log.debug("operation log: {}",operationLog);
            BaseResponse<?> saveRes = logService.saveOperationLog(operationLog);
            if(ResponseCodeConstant.STATUS_FAIL.equals(saveRes.getStatus())){
                log.error("save operation log error: {}", saveRes.getMsg());
            }

        }
        return joinPoint.proceed(args);
    }
}
