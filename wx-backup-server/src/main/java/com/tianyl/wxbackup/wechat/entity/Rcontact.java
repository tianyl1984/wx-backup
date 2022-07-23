package com.tianyl.wxbackup.wechat.entity;

import com.tianyl.wxbackup.mapper.core.Column;
import com.tianyl.wxbackup.mapper.core.Id;

public class Rcontact {

    @Id
    private String username;

    private String alias;

    @Column("conRemark")
    private String remark;

    private String nickname;

    private Integer type;

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
}
