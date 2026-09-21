#!/usr/bin/env bash
# 一键拉起 Nabu 全部依赖 + 应用容器。
# 用法：
#   ./deploy/scripts/up.sh              # 拉起全部（基础设施+中间件+可观测性+应用）
#   ./deploy/scripts/up.sh infra        # 只拉起基础设施（MySQL/Redis/ES/RustFS）
#   ./deploy/scripts/up.sh middleware   # 拉起基础设施+中间件（Nacos/Sentinel/RocketMQ/Seata/Canal/Higress）
#   ./deploy/scripts/up.sh obs          # 拉起基础设施+中间件+可观测性（不含应用，适合本地跑 Java 服务调试）
set -euo pipefail
cd "$(dirname "$0")/.."

FILES=(-f compose.yml -f compose.infrastructure.yml)

case "${1:-all}" in
  infra)
    ;;
  middleware)
    FILES+=(-f compose.middleware.yml)
    ;;
  obs)
    FILES+=(-f compose.middleware.yml -f compose.observability.yml)
    ;;
  all)
    FILES+=(-f compose.middleware.yml -f compose.observability.yml -f compose.app.yml)
    ;;
  *)
    echo "未知参数: $1 (可选 infra|middleware|obs|all)" >&2
    exit 1
    ;;
esac

docker compose --env-file .env "${FILES[@]}" up -d
