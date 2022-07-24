package com.tianyl.wxbackup.entity;

import java.util.Objects;

public class ChatRoom {

    // name对应contact中的username
    private String name;

    private String members;

    private String displayNames;

    private Long modifyTime;

    private String roomOwner;

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(String displayNames) {
        this.displayNames = displayNames;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRoomOwner() {
        return roomOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return name.equals(chatRoom.name) && Objects.equals(members, chatRoom.members)
                && Objects.equals(displayNames, chatRoom.displayNames)
                && Objects.equals(modifyTime, chatRoom.modifyTime)
                && Objects.equals(roomOwner, chatRoom.roomOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, members, displayNames, modifyTime, roomOwner);
    }

    public void setRoomOwner(String roomOwner) {
        this.roomOwner = roomOwner;
    }
}
