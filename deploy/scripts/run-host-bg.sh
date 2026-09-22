#!/usr/bin/env bash
# 裸机批量启动 12 个 Java 微服务（前台常驻，JVM 为本脚本的子进程）。
# 前置：cd deploy && ./scripts/up.sh obs 已起中间件；mvn -B -DskipTests package 已产出 fat jar。
# 用法：./deploy/scripts/run-host-bg.sh    （前台常驻；Ctrl-C 或 SIGTERM 停止全部）
set -uo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LOGDIR="$ROOT/deploy/logs"
HEAP="${NABU_HEAP:--Xmx512m -XX:+ExitOnOutOfMemoryError}"
# 启动顺序：被依赖方优先
SERVICES=(nabu-user-service nabu-auth-service nabu-moderation-service
  nabu-forum-service nabu-social-service nabu-stat-service
  nabu-notify-service nabu-search-service nabu-file-service
  nabu-task-service nabu-admin-service nabu-web)
mkdir -p "$LOGDIR"
: > "$LOGDIR/host-pids.txt"
for m in "${SERVICES[@]}"; do
  jar="$ROOT/$m/target/$m-0.1.0-SNAPSHOT.jar"
  if [ ! -f "$jar" ]; then echo "skip $m (fat jar 缺失)"; continue; fi
  : > "$LOGDIR/$m.log"
  java $HEAP -jar "$jar" > "$LOGDIR/$m.log" 2>&1 &
  echo "$m $!" >> "$LOGDIR/host-pids.txt"
  echo "launched $m pid=$!"
  sleep 2
done
echo "=== all launched $(date '+%T'); waiting (Ctrl-C/SIGTERM stops all) ==="
cleanup() { echo "stopping all..."; pkill -P $$ 2>/dev/null; }
trap cleanup TERM INT
wait
