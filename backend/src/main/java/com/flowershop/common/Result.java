package com.flowershop.common;

import lombok.Data;

/**
 * 统一返回结果封装类
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    private Result() {}

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }

    public static <T> Result<T> unauthorized() {
        return error(401, "未登录或token已过期");
    }

    public static <T> Result<T> forbidden() {
        return error(403, "无权限访问");
    }
}