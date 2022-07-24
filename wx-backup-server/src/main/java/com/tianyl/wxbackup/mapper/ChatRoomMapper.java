package com.tianyl.wxbackup.mapper;

import com.tianyl.wxbackup.db.ConnectionManager;
import com.tianyl.wxbackup.entity.ChatRoom;
import com.tianyl.wxbackup.mapper.core.BaseMapper;

import java.sql.Connection;

public class ChatRoomMapper extends BaseMapper<ChatRoom> {

    @Override
    public Connection getConnection() {
        return ConnectionManager.getConn();
    }

}
