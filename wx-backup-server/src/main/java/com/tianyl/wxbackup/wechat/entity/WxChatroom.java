package com.tianyl.wxbackup.wechat.entity;

import com.tianyl.wxbackup.mapper.core.Table;

@Table("chatroom")
public class WxChatroom {

    private String chatroomname;
    private String memberlist;
    private String displayname;
    private Long modifytime;
    private String roomowner;

    public String getChatroomname() {
        return chatroomname;
    }

    public void setChatroomname(String chatroomname) {
        this.chatroomname = chatroomname;
    }

    public String getMemberlist() {
        return memberlist;
    }

    public void setMemberlist(String memberlist) {
        this.memberlist = memberlist;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public Long getModifytime() {
        return modifytime;
    }

    public void setModifytime(Long modifytime) {
        this.modifytime = modifytime;
    }

    public String getRoomowner() {
        return roomowner;
    }

    public void setRoomowner(String roomowner) {
        this.roomowner = roomowner;
    }
}
