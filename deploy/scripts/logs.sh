#!/usr/bin/env bash
# 查看指定服务的日志，例如：./deploy/scripts/logs.sh nabu-user-service
set -euo pipefail
cd "$(dirname "$0")/.."
source "$(dirname "$0")/ensure-env.sh"
docker compose --env-file .env \
  -f compose.yml -f compose.infrastructure.yml -f compose.middleware.yml \
  -f compose.observability.yml -f compose.app.yml \
  logs -f "$@"
