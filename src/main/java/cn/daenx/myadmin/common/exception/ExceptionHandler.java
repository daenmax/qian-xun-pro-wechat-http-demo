package cn.daenx.myadmin.common.exception;

import cn.daenx.myadmin.common.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;



/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    /**
     * 拦截未知的运行时异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String msg = e.getMessage();
        if (msg.contains("Required request body is missing")) {
            return Result.error("请求数据不能为空");
        }
        log.error("请求地址->{},发生未知异常.", request.getRequestURI(), e);
        return Result.error(msg);
    }

    /**
     * 系统异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public Result handleException(Exception e, HttpServletRequest request) {
        log.error("请求地址->{},发生系统异常.", request.getRequestURI(), e);
        return Result.error(e.getMessage());
    }



    /**
     * 请求方式不支持
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("请求地址->{},不支持->{}请求模式", request.getRequestURI(), e.getMethod());
        return Result.error(e.getMessage());
    }

    /**
     * 上传文件大小超出异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        String msg = "超过系统设定的最大上传文件大小";
        log.error("请求地址->{}, 超过系统设定的最大上传文件大小", request.getRequestURI(), e.getMessage());
        return Result.error(msg);
    }

    /**
     * MyAdmin自定义异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MyException.class)
    public Result handleMyException(MyException e) {
//        log.error(e.getMessage(), e);
        log.error(e.getMessage());
        String msg = e.getMessage();
        return Result.error(msg);
    }

    /**
     * 参数校验异常
     * 基于表单提交时,参数校验异常,会抛出:BindException
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) {
//        log.error(e.getMessage(), e);
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.error(msg);
    }

    /**
     * 参数校验异常
     * 基于json提交时,参数校验异常,会抛出:MethodArgumentNotValidException
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        log.error(e.getMessage(), e);
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(msg);
    }
}
