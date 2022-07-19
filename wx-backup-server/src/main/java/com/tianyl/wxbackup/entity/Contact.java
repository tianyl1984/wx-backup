package com.tianyl.wxbackup.entity;

import com.tianyl.wxbackup.mapper.core.Id;

//@Table("contact")
public class Contact {

    @Id
    private String username;

    private String alias;

    private String remark;

    private String nickname;

    private Integer type;

    private Integer wxType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getWxType() {
        return wxType;
    }

    public void setWxType(Integer wxType) {
        this.wxType = wxType;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "username='" + username + '\'' +
                ", alias='" + alias + '\'' +
                ", remark='" + remark + '\'' +
                ", nickname='" + nickname + '\'' +
                ", type=" + type +
                ", wxType=" + wxType +
                '}';
    }
}
