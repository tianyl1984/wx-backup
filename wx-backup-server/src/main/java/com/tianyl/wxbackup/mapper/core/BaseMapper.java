package com.tianyl.wxbackup.mapper.core;

import com.tianyl.wxbackup.core.Utils;
import com.tianyl.wxbackup.db.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseMapper<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseMapper.class);

    public abstract Connection getConnection();

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

    public int save(T t) {
        // insert into tab(id,name) value(?,?)
        String sql = getInsertSql();
        List<Object> values = getValues(t);
        return executeSql(sql, values.toArray());
    }

    public int update(T t) {
        // update tab set a = ? , b = ? , c = ? where id = ?
        String tab = getTable();
        String idColumn = getIdColumn();
        Object id = getPrimaryKeyValue(t);
        List<Object> values = new ArrayList<>();
        Field[] fields = getGenericClass().getDeclaredFields();
        String sql = String.format("update %s set ", tab);
        for (Field field : fields) {
            boolean isPK = isPrimaryKey(field);
            if (isPK) {
                continue;
            }
            Object value = getFieldValue(field, t);
            if (value == null) {
                continue;
            }
            values.add(value);
            sql += String.format("%s = ?,", getColumn(field));
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += String.format(" where %s = ?", idColumn);
        values.add(id);
        return executeSql(sql, values.toArray());
    }

    private Object getFieldValue(Field field, T t) {
        field.setAccessible(true);
        try {
            return field.get(t);
        } catch (IllegalAccessException e) {
            logger.error("get field value exception", e);
            throw new RuntimeException(e);
        }
    }

    private Object getPrimaryKeyValue(T t) {
        Field[] fields = getGenericClass().getDeclaredFields();
        for (Field field : fields) {
            boolean isPK = isPrimaryKey(field);
            if (!isPK) {
                continue;
            }
            return getFieldValue(field, t);
        }
        throw new RuntimeException("未标记id");
    }

    private String getInsertSql() {
        String tab = getTable();
        List<String> columns = getAllColumns();
        List<String> params = columns.stream().map(e -> "?").collect(Collectors.toList());
        return String.format("insert into %s(%s) values(%s)", tab, String.join(",", columns), String.join(",",
                params));
    }

    public void saveBatch(List<T> list) {
        List<List<T>> dataList = Utils.splitList(list);
        for (List<T> subList : dataList) {
            doSaveBatch(subList);
        }
    }

    private void doSaveBatch(List<T> subList) {
        String sql = getInsertSql();
        withConn(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql);
            for (T t : subList) {
                List<Object> values = getValues(t);
                for (int i = 0; i < values.size(); i++) {
                    ps.setObject(i + 1, values.get(i));
                }
                ps.addBatch();
            }
            ps.executeBatch();
            return null;
        });
    }

    private List<Object> getValues(T t) {
        List<Object> result = new ArrayList<>();
        Field[] fields = getGenericClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                result.add(field.get(t));
            } catch (IllegalAccessException e) {
                logger.error("get field value exception", e);
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private List<String> getAllColumns() {
        return Arrays.stream(getGenericClass().getDeclaredFields()).map(this::getColumn).collect(Collectors.toList());
    }

    private <R> R withConn(Func<Connection, R> func) {
        Connection conn = getConnection();
        try {
            try {
                return func.apply(conn);
            } catch (SQLException e) {
                logger.error("execute sql error", e);
                throw new RuntimeException(e);
            }
        } finally {
            ConnectionManager.close(conn);
        }
    }

    public List<T> getAll() {
        String tab = getTable();
        String sql = String.format("select * from %s ", tab);
        return getList(sql);
    }

    private List<T> getList(String sql, Object... params) {
        return withConn(conn -> {
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
        });
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
            return Utils.getSnakeCase(field.getName());
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
        Table[] tables = clazz.getAnnotationsByType(Table.class);
        if (tables.length == 0) {
            return Utils.getSnakeCase(clazz.getSimpleName());
        }
        return tables[0].value();
    }

    private Class<T> getGenericClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    public void createTable() {
//        CREATE TABLE COMPANY(
//                ID INT PRIMARY KEY     NOT NULL,
//                NAME           TEXT    NOT NULL,
//                AGE            INT     NOT NULL,
//                ADDRESS        CHAR(50),
//                SALARY         REAL
//        )
        Field[] fields = getGenericClass().getDeclaredFields();
        String tab = getTable();
        String sql = "CREATE TABLE " + tab + "(";
        for (Field field : fields) {
            String column = getColumn(field);
            String sqlType = getColumnType(field);
            boolean isPrimaryKey = isPrimaryKey(field);
            sql += column + "  " + sqlType + (isPrimaryKey ? " PRIMARY KEY NOT NULL " : "");
            sql += ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        executeSql(sql);
    }

    public int executeSql(String sql, Object... args) {
        logger.info("execute sql:" + sql);
        return withConn(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
            return ps.executeUpdate();
        });
    }

    private boolean isPrimaryKey(Field field) {
        Id[] ids = field.getAnnotationsByType(Id.class);
        return ids.length > 0;
    }

    private String getColumnType(Field field) {
        Class<?> type = field.getType();
        if (String.class == type) {
            return "TEXT";
        } else if (Integer.class == type) {
            return "INT";
        } else {
            throw new RuntimeException("not support type:" + type);
        }
    }
}
