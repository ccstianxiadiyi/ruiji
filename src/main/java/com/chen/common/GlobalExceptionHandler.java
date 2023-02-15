package com.chen.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/*
 * 全局异常捕获处理
 * */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        if (exception.getMessage().contains("Duplicate entry")) {
            return R.error("账号重复");
        }
        return R.error("网络异常过会再试吧");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> categoryDeleteException(CustomException ex) {
        return R.error(ex.getMessage());
    }
}
