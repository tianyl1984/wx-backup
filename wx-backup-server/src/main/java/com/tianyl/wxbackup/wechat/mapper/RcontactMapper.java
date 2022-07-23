package com.tianyl.wxbackup.wechat.mapper;

import com.tianyl.wxbackup.db.ConnectionManager;
import com.tianyl.wxbackup.mapper.core.BaseMapper;
import com.tianyl.wxbackup.wechat.entity.Rcontact;

import java.sql.Connection;

public class RcontactMapper extends BaseMapper<Rcontact> {

    private String dbFilePath;

    public RcontactMapper(String dbFilePath) {
        this.dbFilePath = dbFilePath;
    }

    @Override
    public Connection getConnection() {
        return ConnectionManager.getConn(dbFilePath);
    }

}
