#!/usr/bin/env bash
# 把 deploy/.env 里的"凭据类"变量导出到当前 shell，供裸机运行的 Java 进程读取。
# 只导出 *PASSWORD / *USERNAME / *ACCESS_KEY / *SECRET_KEY：.env 里的 *_HOST / *_PORT
# 是容器网络地址，裸机跑必须继续用 application.yml 里的 127.0.0.1 默认值，不能覆盖。
# 用法：source deploy/scripts/dev-env.sh    （之后 mvn spring-boot:run / java -jar 即可认证）
_src="${BASH_SOURCE[0]:-$0}"
_deploy="$(cd "$(dirname "$_src")/.." && pwd)"
_envfile="$_deploy/.env"
if [[ ! -f $_envfile ]]; then
  echo "[dev-env] 缺少 $_envfile，请先 cp $_deploy/.env.example $_envfile" >&2
  return 1
fi
while IFS='=' read -r _key _value; do
  [[ $_key =~ (PASSWORD|USERNAME|ACCESS_KEY|SECRET_KEY)$ ]] && export "$_key=$_value"
done < <(grep -vE '^[[:space:]]*(#|$)' "$_envfile")
unset _src _deploy _envfile _key _value
