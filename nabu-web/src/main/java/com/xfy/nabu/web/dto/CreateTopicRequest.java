package com.xfy.nabu.web.dto;

/**
 * 创建帖子请求体。作者 id 不由前端传入，而是从 {@code TraceContext.getUserId()} 取当前登录用户。
 */
public class CreateTopicRequest {

    private Long boardId;
    private String title;
    private String content;

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
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
}
