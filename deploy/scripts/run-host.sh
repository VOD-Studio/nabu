#!/usr/bin/env bash
# 裸机批量启动 12 个 Java 微服务（tmux 托管，脱离 exec 会话存活）。
# 前置：cd deploy && ./scripts/up.sh obs 已起好中间件；且 mvn -B -DskipTests package 已产出 fat jar。
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SESSION="${NABU_TMUX_SESSION:-nabu}"
LOGDIR="$ROOT/deploy/logs"
HEAP="${NABU_HEAP:--Xmx512m -XX:+ExitOnOutOfMemoryError}"
# 启动顺序：被依赖方优先（user/auth 先起，web/admin 最后）
SERVICES=(nabu-user-service nabu-auth-service nabu-moderation-service
  nabu-forum-service nabu-social-service nabu-stat-service
  nabu-notify-service nabu-search-service nabu-file-service
  nabu-task-service nabu-admin-service nabu-web)

cmd="${1:-start}"
case "$cmd" in
  start)
    mkdir -p "$LOGDIR"
    : > "$LOGDIR/host-pids.txt"
    created=0
    for m in "${SERVICES[@]}"; do
      jar="$ROOT/$m/target/$m-0.1.0-SNAPSHOT.jar"
      if [ ! -f "$jar" ]; then
        echo "skip: $m （fat jar 不存在，先 mvn -B -DskipTests package）"; continue
      fi
      if tmux has-session -t "$SESSION" 2>/dev/null && \
         tmux list-windows -t "$SESSION" -F '#{window_name}' 2>/dev/null | grep -qx "$m"; then
        echo "skip: $m （窗口已存在）"; continue
      fi
      # 口令只来自 deploy/.env；tmux server 不继承调用方环境，所以必须把 source 写进被启动的命令串里。
      launch=". \"$ROOT/deploy/scripts/dev-env.sh\" && exec java $HEAP -jar \"$jar\""
      if [ "$created" = 0 ] && ! tmux has-session -t "$SESSION" 2>/dev/null; then
        tmux new-session -d -s "$SESSION" -n "$m" \
          "bash -c '$launch' 2>&1 | tee \"$LOGDIR/$m.log\""
      else
        tmux new-window -a -t "$SESSION" -n "$m" \
          "bash -c '$launch' 2>&1 | tee \"$LOGDIR/$m.log\""
      fi
      created=1
      echo "launched: $m"
      sleep 2   # 错峰启动，减轻 Nacos/DB 同时压力
    done
    echo "--- tmux 窗口 ---"
    tmux list-windows -t "$SESSION" -F '#{window_name}' 2>/dev/null
    echo "查看: tmux attach -t $SESSION  （Ctrl+b d 退出）  停止: $0 stop"
    ;;
  stop)
    if tmux has-session -t "$SESSION" 2>/dev/null; then
      tmux kill-session -t "$SESSION" && echo "stopped: $SESSION"
    else
      echo "no session: $SESSION"
    fi
    ;;
  status)
    if tmux has-session -t "$SESSION" 2>/dev/null; then
      tmux list-windows -t "$SESSION" -F '#{window_name}  active=#{window_active}'
    else
      echo "no session: $SESSION"
    fi
    ;;
  *)
    echo "usage: $0 {start|stop|status}"; exit 1
    ;;
esac
