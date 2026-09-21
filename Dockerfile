# =====================================================================================
# 通用多阶段构建 Dockerfile：通过 --build-arg MODULE=<模块名> 复用同一份 Dockerfile
# 构建全部 13 个 Java 微服务镜像，避免为每个服务各写一份几乎相同的 Dockerfile。
#
# 构建示例：
#   docker build --build-arg MODULE=nabu-user-service -t nabu/nabu-user-service:local .
#
# docker-compose.app.yml 里通过 build.args.MODULE 指定各服务的模块名。
# =====================================================================================

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY nabu-common/pom.xml nabu-common/pom.xml
COPY nabu-api/pom.xml nabu-api/pom.xml
COPY nabu-auth-service/pom.xml nabu-auth-service/pom.xml
COPY nabu-user-service/pom.xml nabu-user-service/pom.xml
COPY nabu-forum-service/pom.xml nabu-forum-service/pom.xml
COPY nabu-social-service/pom.xml nabu-social-service/pom.xml
COPY nabu-notify-service/pom.xml nabu-notify-service/pom.xml
COPY nabu-search-service/pom.xml nabu-search-service/pom.xml
COPY nabu-file-service/pom.xml nabu-file-service/pom.xml
COPY nabu-moderation-service/pom.xml nabu-moderation-service/pom.xml
COPY nabu-stat-service/pom.xml nabu-stat-service/pom.xml
COPY nabu-task-service/pom.xml nabu-task-service/pom.xml
COPY nabu-admin-service/pom.xml nabu-admin-service/pom.xml
COPY nabu-web/pom.xml nabu-web/pom.xml

# 先只拷贝各模块 pom.xml 下载依赖，最大化利用 Docker layer 缓存；
# 源码变更时不会重新下载全部依赖。
RUN mvn -q -B dependency:go-offline || true

COPY . .

ARG MODULE
RUN test -n "$MODULE" || (echo "必须通过 --build-arg MODULE=<模块名> 指定要构建的服务" && exit 1)
RUN mvn -q -B -pl ${MODULE} -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
ARG MODULE
WORKDIR /app
COPY --from=build /workspace/${MODULE}/target/*.jar app.jar

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
