package com.tianyl.wxbackup.db;

import com.tianyl.wxbackup.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    public static Connection getConn() {
        String backupDb = Config.DB_DIR + "/wechat-backup.db";
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + backupDb);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("close connection error", e);
            }
        }
    }

}
