package com.xfy.nabu.social.constant;

/**
 * 社交服务 Redis key 命名规约。全部以 {@code nabu:} 为前缀，风格延续 {@link com.xfy.nabu.common.constant.CommonConstants#REDIS_KEY_PREFIX}。
 */
public final class SocialRedisKeys {

    private SocialRedisKeys() {
    }

    /**
     * 某个业务目标（话题/评论）的点赞用户集合，Redis Set，member 为 userId。
     *
     * @param targetType TOPIC / COMMENT
     * @param targetId   目标 id
     */
    public static String likeSetKey(String targetType, Long targetId) {
        return "nabu:like:" + targetType + ":" + targetId;
    }

    /**
     * followerId 关注的人集合，Redis Set，member 为 followeeId。
     */
    public static String followingSetKey(Long followerId) {
        return "nabu:follow:following:" + followerId;
    }

    /**
     * followeeId 的粉丝集合，Redis Set，member 为 followerId。
     */
    public static String followersSetKey(Long followeeId) {
        return "nabu:follow:followers:" + followeeId;
    }

    /**
     * 某个用户某个月份的签到记录，Redis Bitmap，offset 为当月第几天（从 0 开始）。
     *
     * @param userId    用户 id
     * @param yyyyMM    年月，例如 202601
     */
    public static String checkinBitmapKey(Long userId, String yyyyMM) {
        return "nabu:checkin:" + userId + ":" + yyyyMM;
    }
}
