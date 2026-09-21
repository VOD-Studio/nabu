package com.xfy.nabu.notify.domain;

import com.xfy.nabu.common.base.BaseEntity;

public class NotificationEntity extends BaseEntity {

    private Long receiverId;
    /** 通知类型：COMMENT_REPLY / LIKE / MENTION / SYSTEM / FOLLOW */
    private String type;

    private String content;
    private String linkUrl;
    /** 是否已读：0-未读，1-已读 */
    private Integer isRead = 0;

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}
