package com.tianyl.wxbackup.entity;

public class Message {
    //msgId isSend talker content

    private Integer msgId;

    private Integer createTime;

    // 1: 表示自己发出的消息； 0: 表示接受消息； 2: 其他消息，出现的包括群语音通话和我发起的拉人入群
    private Integer isSend;

    private String talker;

    private String content;

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getIsSend() {
        return isSend;
    }

    public void setIsSend(Integer isSend) {
        this.isSend = isSend;
    }

    public String getTalker() {
        return talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
