package com.fkp.aspect;

import com.alibaba.fastjson.JSON;
import com.fkp.controller.TargetTestController;
import com.fkp.entity.User;
import com.fkp.exception.BusinessException;
import com.fkp.param.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.SourceLocation;
import org.springframework.stereotype.Component;

/**
 * 切面类，需要加入到容器中
 */
@Aspect
@Component
@Slf4j
public class MyAspect {

    /**
     * 定义切点
     * execution(访问权限 方法返回值 方法声明(参数) 异常类型);
     *      *代表任意个数
     *      ..用在方法参数中，表示任意多个参数,用在包名后，表示当前包及其子包路径
     *      +用在类名后，表示当前类及其子类，用在接口后，表示当前接口及其实现类
     */
    @Pointcut(value = "execution(public * com.fkp.controller.Target*.*(..))")
    public void myPointCut(){
    }

    /**
     * 定义前置增强
     *
     * @param joinPoint 可以获得目标方法的信息
     */
    @Before(value = "myPointCut()")
    public void beforeAdvice(JoinPoint joinPoint){
        log.info("----------------------------Before----------------------------");
        //拿到目标方法的参数列表
        Object[] args = joinPoint.getArgs();
        log.info("method args: {}", args);
        //拿到方法的签名信息，可以通过其获取方法名
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        log.info("method name: {}", methodName);
        //拿到目标方法所在的类对象，可以调用其方法
        Object target = joinPoint.getTarget();
        TargetTestController targetObject = (TargetTestController) target;
        BaseResponse<User> response = targetObject.getUserById("002");
        log.info("targetObjectInvocationResponse{}", response);
    }

    /**
     * 后置增强，方法正常执行完成后执行，若发生异常则不执行
     * 参数列表需要在注解属性argNames中标明参数名和顺序，returning属性指定接收目标方法返回值的参数名，JoinPoint需在Object前
     *
     * @param result 获得目标方法执行结果
     * @param joinPoint 用来获取目标方法的信息
     */
    @AfterReturning(value = "myPointCut()", returning = "result", argNames = "joinPoint,result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result){
        log.info("----------------------------AfterReturning----------------------------");
        //获取目标方法执行结果
        log.info("result: {}", result);
        //获取目标方法执行结果的类型
        log.info("resultType: {}", result.getClass());
        //获取目标方法的参数列表
        log.info("method args: {}", joinPoint.getArgs());
    }

    /**
     * 环绕增强，可以在方法前和方法后增强，需要手动调用ProceedingJoinPoint的proceed方法执行目标方法并拿到返回值并返回
     *
     * @param joinPoint 可以获得目标方法的信息以及执行目标方法，ProceedingJoinPoint实现JoinPoint
     * @return 目标方法实际的返回值
     * @throws Throwable ProceedingJoinPoint的proceed方法抛出Throwable类型的异常
     */
    @Around(value = "myPointCut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("----------------------------Around----------------------------");
        //拿到目标方法参数列表
        Object[] args = joinPoint.getArgs();
        log.info("method args: {}",args);
        //拿到目标方法名
        String methodName = joinPoint.getSignature().getName();
        log.info("method name: {}", methodName);
        if("getUserById".equals(methodName)){
//            args[0] = "002";
        }
        //执行目标方法并传入参数
        Object response = joinPoint.proceed(args);
        log.info("target method result: {}", response);

        //若已知返回值的类型可以进行强转并获取其中的属性值
        if(response instanceof BaseResponse){
            BaseResponse<?> baseResponse = (BaseResponse<?>) response;
            log.info("response status: {}", baseResponse.getStatus());
        }

        //设置目标方法的返回值
        return response;
    }

    /**
     * 异常增强，在目标方法抛出异常时执行，可用于异常监控，执行此方法后进入全局异常处理器
     * @AfterThrowing throwing属性指定方法中Exception类型参数名
     *
     * @param joinPoint 可以获取目标方法的信息
     * @param e 发生异常的异常对象
     */
    @AfterThrowing(value = "myPointCut()", throwing = "e", argNames = "joinPoint,e")
    public void throwingAdvice(JoinPoint joinPoint, Exception e){
        log.info("----------------------------AfterThrowing----------------------------");
        //获取目标方法的方法名
        String methodName = joinPoint.getSignature().getName();
        //获取发生异常的异常信息
        String errorMessage = e.getMessage();
        log.error("methodName: {},exception message: {}", methodName, errorMessage);

        //可以对特定的异常进行处理
        if(e instanceof BusinessException){
            log.error("business exception message: {}", e.getMessage());
        }
    }

    /**
     * 最终增强，目标方法执行后执行，总是执行无论是否发生异常，若发生异常则该方法执行后进入全局异常处理器
     *
     * @param joinPoint 可以获取目标方法的信息
     */
    @After(value = "myPointCut()")
    public void afterAdvice(JoinPoint joinPoint){
        log.info("----------------------------After----------------------------");
        //获取参数列表
        Object[] args = joinPoint.getArgs();
        //获取方法名
        String methodName = joinPoint.getSignature().getName();
        log.info("method name: {},args: {}",methodName, args);
    }
}
