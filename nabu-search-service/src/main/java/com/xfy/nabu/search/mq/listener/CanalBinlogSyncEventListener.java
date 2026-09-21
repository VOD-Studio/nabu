package com.xfy.nabu.search.mq.listener;

import com.alibaba.fastjson2.JSON;
import com.xfy.nabu.search.domain.TopicIndexDocument;
import com.xfy.nabu.search.mq.event.CanalBinlogSyncEvent;
import com.xfy.nabu.search.service.SearchBizService;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 消费 "CanalBinlogSyncEvent"：这是 Canal Server 监听 nabu_forum 库 t_topic 表 binlog 后
 * （通过 canal-adapter 或自定义 canal client）投递到 RocketMQ 的消息。
 *
 * <p><b>重要说明：</b>本监听器假定消息体是一个简化的 JSON 结构（见 {@link CanalBinlogSyncEvent}），
 * 字段至少包含 type(INSERT/UPDATE/DELETE)、table、data(Map，t_topic 的列)，这是简化后的实现，
 * <b>不代表</b>真实 Canal Server 的原生投递结构。真实场景下：
 * <ul>
 *     <li>如果自建 canal client 消费 canal-server 的 TCP 协议再转发到 RocketMQ，
 *     消息体应该是 Canal 官方协议 {@code canal.protocol.CanalEntry}（Protobuf），
 *     需要按 RowChange/RowData/Column 结构重新实现本监听器的解析逻辑；</li>
 *     <li>更推荐的做法是直接使用 canal-adapter 的 RocketMQ 投递能力（rocketmq client adapter），
 *     甚至 canal-adapter 本身就支持配置规则把 binlog 直接同步落地到 Elasticsearch，
 *     那样可以完全不需要本服务这段自定义 Java 消费代码。</li>
 * </ul></p>
 *
 * <p>索引写入完全依赖本监听器：INSERT/UPDATE 时 upsert 对应的 {@link TopicIndexDocument} 到 ES；
 * DELETE 时删除对应文档。业务代码（forum-service）不做任何同步双写 ES 的操作。</p>
 *
 * <p>消费幂等说明：ES 的 upsert（{@code save} 按 id 覆盖写）本身是天然幂等的，
 * 重复消费同一条 INSERT/UPDATE 事件只会覆盖成同样的最终结果，无需额外去重表。</p>
 */
@Component
@RocketMQMessageListener(
        topic = "CanalBinlogSyncEvent",
        consumerGroup = "CanalBinlogSyncEvent_SEARCH_CG",
        consumeMode = ConsumeMode.CONCURRENTLY
)
public class CanalBinlogSyncEventListener implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(CanalBinlogSyncEventListener.class);

    private static final String TABLE_TOPIC = "t_topic";

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SearchBizService searchBizService;

    public CanalBinlogSyncEventListener(SearchBizService searchBizService) {
        this.searchBizService = searchBizService;
    }

    @Override
    public void onMessage(String message) {
        CanalBinlogSyncEvent event;
        try {
            event = JSON.parseObject(message, CanalBinlogSyncEvent.class);
        } catch (Exception e) {
            log.error("解析 CanalBinlogSyncEvent 消息失败，忽略：{}", message, e);
            return;
        }
        if (event == null || !TABLE_TOPIC.equals(event.getTable())) {
            log.debug("忽略非 t_topic 表的 binlog 同步事件：{}", event == null ? null : event.getTable());
            return;
        }
        Map<String, Object> data = event.getData();
        if (data == null || data.get("id") == null) {
            log.warn("CanalBinlogSyncEvent 缺少主键 id，忽略：{}", message);
            return;
        }
        String id = String.valueOf(data.get("id"));
        try {
            switch (event.getType()) {
                case "INSERT":
                case "UPDATE":
                    searchBizService.upsert(toDocument(id, data));
                    break;
                case "DELETE":
                    searchBizService.delete(id);
                    break;
                default:
                    log.warn("未知的 binlog 变更类型，忽略：type={}", event.getType());
            }
        } catch (Exception e) {
            log.error("处理 CanalBinlogSyncEvent 失败：{}", message, e);
            throw e;
        }
    }

    private TopicIndexDocument toDocument(String id, Map<String, Object> data) {
        TopicIndexDocument document = new TopicIndexDocument();
        document.setId(id);
        document.setBoardId(toLong(data.get("board_id")));
        document.setAuthorId(toLong(data.get("author_id")));
        document.setTitle(data.get("title") == null ? null : String.valueOf(data.get("title")));
        document.setContent(data.get("content") == null ? null : String.valueOf(data.get("content")));
        document.setCreatedAtEpochMillis(toEpochMillis(data.get("created_at")));
        return document;
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    /**
     * t_topic.created_at 在 MySQL 中是 DATETIME，Canal 投递的简化 JSON 里假定以
     * "yyyy-MM-dd HH:mm:ss" 格式的字符串表示（真实 Canal 协议里通常是字符串或时间戳，需按实际情况调整）。
     */
    private long toEpochMillis(Object value) {
        if (value == null) {
            return System.currentTimeMillis();
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(value), DATETIME_FORMATTER);
            return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception e) {
            log.warn("解析 created_at 失败，使用当前时间兜底：{}", value);
            return System.currentTimeMillis();
        }
    }
}
