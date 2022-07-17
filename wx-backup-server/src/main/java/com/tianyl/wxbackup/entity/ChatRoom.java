package com.tianyl.wxbackup.entity;

public class ChatRoom {

    // name对应contact中的username
    private String name;

    private String members;

    private String displaynames;

    private Integer modifytime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getDisplaynames() {
        return displaynames;
    }

    public void setDisplaynames(String displaynames) {
        this.displaynames = displaynames;
    }

    public Integer getModifytime() {
        return modifytime;
    }

    public void setModifytime(Integer modifytime) {
        this.modifytime = modifytime;
    }
}
