package com.summit.chat.Result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg;
    private T data;
    private String sign;

    public static  <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "success";
        result.data = null;
        return result;
    }

    public static <T> Result<T> ok(T t) {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "success";
        result.data = t;

        return result;
    }

    public static <T> Result<T> fail(String t) {
        Result<T> result = new Result<>();
        result.code = 0;
        result.msg = t;
        return result;
    }

}