package com.fkp.handler;

import com.fkp.constant.ErrorCodeEnum;
import com.fkp.exception.BusinessException;
import com.fkp.param.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.net.ConnectException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理bean validation 异常
     *
     * @param e BindException及其子类对象
     * @return 统一返回错误信息
     */
    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    public BaseResponse<?> validException(BindException e){
        e.printStackTrace();
        StringBuilder sb = new StringBuilder();
        try {
            for (FieldError error : e.getFieldErrors()) {
                log.error("GlobalExceptionHandler -- location:{}#{} -- message:{}",error.getObjectName(),error.getField(),error.getDefaultMessage());
                String message = error.getDefaultMessage();
                if (StringUtils.isNotBlank(message)) {
                    sb.append(message).append(";");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return BaseResponse.fail(ErrorCodeEnum.GlobalInnerException.getCode(), ErrorCodeEnum.GlobalInnerException.getMsg() + ": " + ex.getMessage());
        }
        return BaseResponse.fail(ErrorCodeEnum.ValidException.getCode(),sb.toString());
    }

    /**
     * 处理方法参数的校验，类上加@Validate,校验在方法参数上
     *
     * @param e 异常对象 ConstraintViolationException
     * @return 统一返回错误信息
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public BaseResponse<?> validException2(ConstraintViolationException e){
        e.printStackTrace();
        StringBuilder sb = new StringBuilder();
        try {
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                log.error("GlobalExceptionHandler -- location:{}#{} -- message:{}", violation.getRootBeanClass(), violation.getPropertyPath(),violation.getMessage());
                String message = violation.getMessage();
                if (StringUtils.isNotBlank(message)) {
                    sb.append(message).append(";");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return BaseResponse.fail(ErrorCodeEnum.GlobalInnerException.getCode(),ErrorCodeEnum.GlobalInnerException.getMsg() + ": " + ex.getMessage());
        }
        return BaseResponse.fail(ErrorCodeEnum.ValidException.getCode(),sb.toString());
    }

    /**
     * 业务异常
     * @param e 异常对象
     * @return 返回统一错误信息
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessError(BusinessException e){
        e.printStackTrace();
        String errorCode = e.getErrorCode();
        if(StringUtils.isBlank(errorCode)){
            errorCode = ErrorCodeEnum.BusinessException.getCode();
        }
        String errorMessage = e.getMessage();
        if(StringUtils.isBlank(errorMessage)){
            errorMessage = ErrorCodeEnum.BusinessException.getMsg();
        }
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 连接异常
     *
     * @param e 异常对象ConnectException
     * @return 统一返回错误信息
     */
    @ExceptionHandler(ConnectException.class)
    public BaseResponse<?> connectionError(ConnectException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.NetworkError.getCode();
        String errorMessage = ErrorCodeEnum.NetworkError.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 数据库连接异常
     *
     * @param e 异常对象 CommunicationsException
     * @return 统一返回错误信息
     */
//    @ExceptionHandler(CommunicationsException.class)
//    public BaseResponse<?> communicationsException(CommunicationsException e) {
//            e.printStackTrace();
//        String errorCode = ErrorCodeEnum.DatabaseException.getCode();
//        String errorMessage = ErrorCodeEnum.DatabaseException.getMsg() + ": " + e.getMessage();
//        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
//        return BaseResponse.fail(errorCode, errorMessage);
//    }

    /**
     * 无法使用 JTA 等底层事务 API 创建事务时引发异常
     *
     * @param e 异常对象 CannotCreateTransactionException
     * @return 统一返回错误信息
     */
//    @ExceptionHandler(CannotCreateTransactionException.class)
//    public BaseResponse<?> cannotCreateTransactionException(CannotCreateTransactionException e) {
//            e.printStackTrace();
//        String errorCode = ErrorCodeEnum.CannotCreateTransactionException.getCode();
//        String errorMessage = ErrorCodeEnum.CannotCreateTransactionException.getMsg() + ": " + e.getMessage();
//        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
//        return BaseResponse.fail(errorCode, errorMessage);
//    }

    /**
     * 遇到一般事务系统错误时抛出异常，例如提交或回滚
     *
     * @param e 异常对象 TransactionSystemException
     * @return 统一返回错误信息
     */
//    @ExceptionHandler(TransactionSystemException.class)
//    public BaseResponse<?> transactionSystemException(TransactionSystemException e) {
//            e.printStackTrace();
//        String errorCode = ErrorCodeEnum.TransactionSystemException.getCode();
//        String errorMessage = ErrorCodeEnum.TransactionSystemException.getMsg() + ": " + e.getMessage();
//        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
//        return BaseResponse.fail(errorCode, errorMessage);
//    }

    /**
     * 类型转换异常
     *
     * @param e 异常对象 ClassCastException
     * @return 统一返回错误信息
     */
    @ExceptionHandler(ClassCastException.class)
    public BaseResponse<?> classCastException(ClassCastException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.ClassCastException.getCode();
        String errorMessage = ErrorCodeEnum.ClassCastException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 未知方法异常
     *
     * @param e 异常对象 NoSuchMethodException
     * @return 统一返回错误信息
     */
    @ExceptionHandler(NoSuchMethodException.class)
    public BaseResponse<?> noSuchMethodException(NoSuchMethodException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.NoSuchMethodException.getCode();
        String errorMessage = ErrorCodeEnum.NoSuchMethodException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 数组越界异常
     *
     * @param e 异常对象 IndexOutOfBoundsException
     * @return 统一返回错误信息
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public BaseResponse<?> indexOutOfBoundsException(IndexOutOfBoundsException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.IndexOutOfBoundsException.getCode();
        String errorMessage = ErrorCodeEnum.IndexOutOfBoundsException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 空指针异常
     *
     * @param e 异常对象 NullPointerException
     * @return 统一返回错误信息
     */
    @ExceptionHandler(NullPointerException.class)
    public BaseResponse<?> nullPointerExceptionHandler(NullPointerException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.NullPointerException.getCode();
        String errorMessage = ErrorCodeEnum.NullPointerException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * IO异常
     *
     * @param e 异常对象 IOException
     * @return 统一返回错误信息
     */
    @ExceptionHandler(IOException.class)
    public BaseResponse<?> iOExceptionHandler(IOException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.IOException.getCode();
        String errorMessage = ErrorCodeEnum.IOException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 400错误 参数格式错误
     *
     * @param e 异常对象 HttpMessageNotReadableException
     * @return 统一返回错误信息
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public BaseResponse<?> requestNotReadable(HttpMessageNotReadableException e) {
        e.printStackTrace();
        String returnMsg = StringUtils.EMPTY;
        String message = e.getMessage();
        if (StringUtils.isNotBlank(message)) {
            returnMsg = message.substring(message.lastIndexOf(("field : ")));
        }
        String errorCode = ErrorCodeEnum.ParamsInvalid.getCode();
        String errorMessage = returnMsg + StringUtils.SPACE + ErrorCodeEnum.ParamsInvalid.getMsg();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 400错误 参数缺失错误
     *
     * @param e 异常对象MissingServletRequestParameterException
     * @return 统一返回错误信息
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public BaseResponse<?> requestMissingServletRequest(MissingServletRequestParameterException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.ParamMissing.getCode();
        String errorMessage = ErrorCodeEnum.ParamMissing.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 400错误 尝试设置 bean 属性时在类型不匹配时引发异常
     *
     * @param e 异常对象 TypeMismatchException
     * @return 统一返回错误信息
     */
    @ExceptionHandler({TypeMismatchException.class})
    public BaseResponse<?> typeMismatchException(TypeMismatchException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.TypeMismatchException.getCode();
        String errorMessage = ErrorCodeEnum.TypeMismatchException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 404错误 找不到资源
     *
     * @param e 异常对象 NoHandlerFoundException
     * @return 统一返回错误信息
     */
    @ExceptionHandler({NoHandlerFoundException.class})
    public BaseResponse<?> request404(NoHandlerFoundException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.NoHandlerFoundException.getCode();
        String errorMessage = ErrorCodeEnum.NoHandlerFoundException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",NoHandlerFoundException.class.toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 405错误 不支持的请求方法
     *
     * @param e 异常对象 HttpRequestMethodNotSupportedException
     * @return 统一返回错误信息
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public BaseResponse<?> request405(HttpRequestMethodNotSupportedException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.HttpRequestMethodNotSupportedException.getCode();
        String errorMessage = ErrorCodeEnum.HttpRequestMethodNotSupportedException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",HttpRequestMethodNotSupportedException.class.toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 415错误 请求的数据类型服务端不支持
     * @param e 异常对象 HttpMediaTypeNotSupportedException
     * @return 统一返回错误信息
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public BaseResponse<?> request415(HttpMediaTypeNotSupportedException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.HttpMediaTypeNotSupportedException.getCode();
        String errorMessage = ErrorCodeEnum.HttpMediaTypeNotSupportedException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",HttpRequestMethodNotSupportedException.class.toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 406错误 当请求处理程序无法生成客户端可接受的响应时引发异常
     *
     * @param e 异常对象 HttpMediaTypeNotAcceptableException
     * @return 统一返回错误信息
     */
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public BaseResponse<?> request406(HttpMediaTypeNotAcceptableException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.HttpMediaTypeNotAcceptableException.getCode();
        String errorMessage = ErrorCodeEnum.HttpMediaTypeNotAcceptableException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",HttpMediaTypeNotAcceptableException.class.toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 500错误 找不到适合 bean 属性的编辑器或转换器时抛出异常
     *
     * @return 统一返回错误信息
     */
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    public BaseResponse<?> server500(RuntimeException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.ConversionNotSupportedException.getCode();
        String errorMessage = ErrorCodeEnum.ConversionNotSupportedException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",HttpMediaTypeNotAcceptableException.class.toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 运行时异常
     *
     * @param e 异常对象 RuntimeException
     * @return 统一返回错误信息
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.RuntimeException.getCode();
        String errorMessage = ErrorCodeEnum.RuntimeException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

    /**
     * 其他异常
     *
     * @param e 异常对象 Exception, Throwable
     * @return 统一返回错误信息
     */
    @ExceptionHandler(value = {Exception.class, Throwable.class})
    public BaseResponse<?> defaultErrorHandler(Throwable e) {
        e.printStackTrace();
        String errorCode = ErrorCodeEnum.OtherException.getCode();
        String errorMessage = ErrorCodeEnum.OtherException.getMsg() + ": " + e.getMessage();
        log.error("GlobalExceptionHandler -- ExceptionType:{} -- ErrorCode:{} -- ErrorMessage:{}",e.getClass().toString(), errorCode, errorMessage);
        return BaseResponse.fail(errorCode, errorMessage);
    }

}
