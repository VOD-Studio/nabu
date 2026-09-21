package com.xfy.nabu.stat.constant;

/**
 * 统计服务 Redis key 命名规约。全部以 {@code nabu:} 为前缀，风格延续 {@link com.xfy.nabu.common.constant.CommonConstants#REDIS_KEY_PREFIX}。
 */
public final class StatRedisKeys {

    private StatRedisKeys() {
    }

    /** 话题 PV 计数，Redis String，INCR 累加 */
    public static String pvKey(Long topicId) {
        return "nabu:stat:pv:" + topicId;
    }

    /** 话题 UV 近似统计，Redis HyperLogLog，PFADD 累加访问者 id */
    public static String uvKey(Long topicId) {
        return "nabu:stat:uv:" + topicId;
    }

    /** 全站热门话题排行榜，Redis ZSet，固定 key，member 为 topicId，score 为热度分 */
    public static final String HOT_TOPICS_KEY = "nabu:stat:hot-topics";
}
