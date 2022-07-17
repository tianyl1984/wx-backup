package com.tianyl.wxbackup.core;

@FunctionalInterface
public interface Func<T, R> {

    R apply(T t);

}
