package com.tianyl.wxbackup;

public class Result<T> {

    private int code;

    private String msg;

    private T result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static Result<?> fail(String msg) {
        Result<?> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> success(T obj) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setResult(obj);
        return result;
    }
    
}
