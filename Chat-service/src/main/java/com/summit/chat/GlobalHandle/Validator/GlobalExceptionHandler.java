package com.summit.chat.GlobalHandle.Validator;


import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handle(MethodArgumentNotValidException e){
        String errorMsg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(errorMsg);
    }
    @ExceptionHandler(Exception.class)
    public Result handleAll(Exception e){
        log.error("【全局异常处理】错误: {}",e.getMessage());
        e.printStackTrace();
        return Result.fail(BaseConstants.SERVER_EXCEPTION);
    }


    @ExceptionHandler(BusinessException.class)
    public Result handle(BusinessException e){
        return Result.fail(e.getMessage());
    }
}
