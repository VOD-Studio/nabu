#!/usr/bin/env bash
# 停止并移除 Nabu 全部容器（不删除数据卷，数据保留在 deploy/data/ 下）。
set -euo pipefail
cd "$(dirname "$0")/.."
source "$(dirname "$0")/ensure-env.sh"

docker compose --env-file .env \
  -f compose.yml \
  -f compose.infrastructure.yml \
  -f compose.middleware.yml \
  -f compose.observability.yml \
  -f compose.app.yml \
  down
