package com.tianyl.wxbackup.mapper;

import com.tianyl.wxbackup.db.ConnectionManager;
import com.tianyl.wxbackup.entity.Contact;
import com.tianyl.wxbackup.mapper.core.BaseMapper;

import java.sql.Connection;

public class ContactMapper extends BaseMapper<Contact> {

    @Override
    public Connection getConnection() {
        return ConnectionManager.getConn();
    }
}
