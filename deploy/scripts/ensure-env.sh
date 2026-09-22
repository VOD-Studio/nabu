#!/usr/bin/env bash
# 确保 deploy/.env 存在：缺失时从可入库的 .env.example 生成一份本地副本。
# deploy/.env 含口令、不入库，所以每个用到 --env-file .env 的脚本都要先 source 本文件。
# 前置条件：调用方已 cd 到 deploy 目录。
if [[ -f .env ]]; then
  return 0
fi
if [[ ! -f .env.example ]]; then
  echo "[ensure-env] 缺少 deploy/.env，也没有 deploy/.env.example 模板，无法继续。" >&2
  return 1
fi
cp .env.example .env
echo "[ensure-env] 已从 .env.example 生成 deploy/.env（占位口令），首次使用请按需修改。" >&2
