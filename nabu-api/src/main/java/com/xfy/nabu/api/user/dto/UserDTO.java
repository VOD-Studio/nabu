package com.xfy.nabu.api.user.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息传输对象，供其他服务通过 Dubbo 调用 user-service 时使用。
 */
public class UserDTO implements Serializable {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private Integer level;
    private Integer points;
    /** 账号状态：0-正常，1-禁用 */
    private Integer status;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
