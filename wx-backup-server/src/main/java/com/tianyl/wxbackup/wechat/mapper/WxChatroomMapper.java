package com.tianyl.wxbackup.wechat.mapper;

import com.tianyl.wxbackup.db.ConnectionManager;
import com.tianyl.wxbackup.mapper.core.BaseMapper;
import com.tianyl.wxbackup.wechat.entity.WxChatroom;

import java.sql.Connection;

public class WxChatroomMapper extends BaseMapper<WxChatroom> {

    private String dbFilePath;

    public WxChatroomMapper(String dbFilePath) {
        this.dbFilePath = dbFilePath;
    }

    @Override
    public Connection getConnection() {
        return ConnectionManager.getConn(dbFilePath);
    }

}
