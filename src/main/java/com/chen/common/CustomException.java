package com.chen.common;

/*
 * 自定义业务异常，用于菜单种类删除判断抛出
 * */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
