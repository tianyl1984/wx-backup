package com.tianyl.wxbackup.mapper.core;

import java.sql.SQLException;

@FunctionalInterface
public interface Func<T, R> {

    R apply(T t) throws SQLException;
}
