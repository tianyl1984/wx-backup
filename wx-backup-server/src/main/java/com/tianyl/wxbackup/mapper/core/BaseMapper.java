package com.tianyl.wxbackup.mapper.core;

import com.tianyl.wxbackup.Utils;
import com.tianyl.wxbackup.core.Func;
import com.tianyl.wxbackup.db.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseMapper<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseMapper.class);

    public T get(Serializable id) {
        String tab = getTable();
        String idColumn = getIdColumn();
        String sql = String.format("select * from %s where %s = ?", tab, idColumn);
        List<T> list = getList(sql, id);
        if (Utils.isEmpty(list)) {
            return null;
        }
        if (list.size() > 1) {
            throw new RuntimeException("expect one but got " + list.size());
        }
        return list.get(0);
    }

    private <R> R withConn(Func<Connection, R> func) {
        return func.apply(ConnectionManager.getConn());
    }

    private List<T> getList(String sql, Object... params) {
        Connection conn = ConnectionManager.getConn();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            ResultSet rs = ps.executeQuery();
            List<String> labels = getResultSetLabels(rs);
            List<Map<String, Object>> resultMap = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (String label : labels) {
                    map.put(label, rs.getObject(label));
                }
                resultMap.add(map);
            }
            List<T> result = new ArrayList<>();
            if (resultMap.size() == 0) {
                return result;
            }
            for (Map<String, Object> map : resultMap) {
                T t = newInstance();
                Field[] fields = t.getClass().getDeclaredFields();
                for (Field field : fields) {
                    String column = getColumn(field);
                    Object value = map.get(column);
                    field.setAccessible(true);
                    try {
                        field.set(t, value);
                    } catch (IllegalAccessException e) {
                        logger.error("set value error:" + field.getName(), e);
                        throw new RuntimeException(e);
                    }
                }
                result.add(t);
            }
            return result;
        } catch (SQLException e) {
            logger.error("execute sql exception", e);
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(conn);
        }
    }

    private T newInstance() {
        Class<T> clazz = getGenericClass();
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("new instance error", e);
            throw new RuntimeException(e);
        }
    }

    private List<String> getResultSetLabels(ResultSet rs) {
        try {
            List<String> labels = new ArrayList<>();
            ResultSetMetaData rsmd = rs.getMetaData();
            int cnt = rsmd.getColumnCount();
            for (int i = 0; i < cnt; i++) {
                labels.add(rsmd.getColumnLabel(i + 1));
            }
            return labels;
        } catch (SQLException e) {
            logger.error("read resultset metadata error", e);
            throw new RuntimeException(e);
        }
    }

    private String getIdColumn() {
        Class<T> clazz = getGenericClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Id[] ids = field.getAnnotationsByType(Id.class);
            if (ids.length > 0) {
                return getColumn(field, ids[0]);
            }
        }
        throw new RuntimeException("未标记id");
    }

    private String getColumn(Field field) {
        Column[] columns = field.getAnnotationsByType(Column.class);
        if (columns.length == 0) {
            return field.getName();
        }
        return columns[0].value();
    }

    private String getColumn(Field field, Id id) {
        if (Utils.isEmpty(id.value())) {
            return field.getName();
        }
        return id.value();
    }

    private String getTable() {
        Class<T> clazz = getGenericClass();
        Table table = clazz.getAnnotationsByType(Table.class)[0];
        return table.value();
    }

    private Class<T> getGenericClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }
}
