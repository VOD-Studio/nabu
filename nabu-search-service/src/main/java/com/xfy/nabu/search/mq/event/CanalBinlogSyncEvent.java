package com.xfy.nabu.search.mq.event;

import java.io.Serializable;
import java.util.Map;

/**
 * "CanalBinlogSyncEvent" 消息体：假定由 Canal Server 监听 nabu_forum 库 t_topic 表 binlog 后，
 * 经由某个投递程序（canal-adapter 或自定义 canal client）转换为该简化 JSON 结构并投递到 RocketMQ。
 *
 * <p><b>重要说明：</b>这是简化后的消息结构，<b>不是</b>真实 Canal Server 的原生协议。
 * 真实 Canal Server（无论是直接消费 canal-server 的 TCP 协议，还是通过 canal-adapter 转发）
 * 投递的消息结构以 Canal 官方协议 {@code canal.protocol.CanalEntry}（Protobuf 定义，包含
 * RowChange / RowData / Column 等结构）为准，字段更丰富（含 before/after 列值、DDL 信息等）。
 * 如果要接入真实 Canal，需要调整本类及 {@link com.xfy.nabu.search.mq.listener.CanalBinlogSyncEventListener}
 * 的消息解析逻辑以匹配 CanalEntry 的实际结构；或者更简单地，直接使用 canal-adapter 内置的
 * RocketMQ/ES 落地能力（canal-adapter 支持配置规则直接把 binlog 同步到 Elasticsearch，
 * 这种情况下可以完全不需要自定义 Java 代码消费 RocketMQ）。</p>
 */
public class CanalBinlogSyncEvent implements Serializable {

    /** 变更类型：INSERT / UPDATE / DELETE */
    private String type;

    /** 变更所属的 MySQL 表名，本服务只关心 t_topic */
    private String table;

    /** 变更后的行数据（列名 -> 列值），DELETE 时至少应包含主键 id */
    private Map<String, Object> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
