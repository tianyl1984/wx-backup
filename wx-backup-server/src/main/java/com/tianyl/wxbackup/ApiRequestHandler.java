package com.tianyl.wxbackup;

public class ApiRequestHandler {

    private static ApiRequestHandler instance = new ApiRequestHandler();

    public static ApiRequestHandler getInstance() {
        return instance;
    }

    public String test(int a, Integer b, long c, Long d) {
        return "a:" + a + ",b:" + b + ",c:" + c + ",d:" + d;
    }
}
