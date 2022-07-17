package com.tianyl.wxbackup.enums;

public enum ContactType {
    FRIEND(1, "好友"),
    GROUP_FRIEND(2, "群聊"),
    OFFICIAL_ACCOUNTS(3, "公众号"),
    ;

    private ContactType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private Integer type;

    private String desc;

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
