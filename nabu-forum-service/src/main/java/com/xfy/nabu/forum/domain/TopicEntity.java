package com.xfy.nabu.forum.domain;

import com.xfy.nabu.common.base.BaseEntity;

/**
 * 帖子实体，对应表 t_topic。
 */
public class TopicEntity extends BaseEntity {

    private Long boardId;
    private Long authorId;
    private String title;
    private String content;
    private Integer viewCount = 0;
    private Integer commentCount = 0;
    private Integer likeCount = 0;
    /** 是否置顶：0-否，1-是 */
    private Integer pinned = 0;
    /** 状态：0-正常，1-审核中，2-已屏蔽 */
    private Integer status = 0;

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getPinned() {
        return pinned;
    }

    public void setPinned(Integer pinned) {
        this.pinned = pinned;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
