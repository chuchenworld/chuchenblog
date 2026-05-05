package com.chuchen.blog.common;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(ClientAbortException e) {
        log.debug("客户端连接中断: {}", e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDeniedException(AccessDeniedException e, HttpServletResponse response) {
        log.error("【全局异常处理】权限不足 - 错误信息: {}", e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return Result.error(403, "没有权限访问该资源");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletResponse response) {
        log.error("【全局异常处理】未捕获的异常 - 错误类型: {}, 错误信息: {}, 堆栈: {}", 
            e.getClass().getName(), e.getMessage(), e.getStackTrace()[0]);
        e.printStackTrace();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        String errorMsg = "服务器内部错误";
        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            // 返回更详细的错误信息给前端
            errorMsg = "服务器内部错误: " + e.getMessage();
        }
        return Result.error(500, errorMsg);
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletResponse response) {
        log.error("【全局异常处理】运行时异常 - 错误类型: {}, 错误信息: {}", 
            e.getClass().getName(), e.getMessage());
        e.printStackTrace();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return Result.error(e.getMessage());
    }
}
