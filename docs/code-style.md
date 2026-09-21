# 代码格式化（Spotless）

项目用 Spotless（`com.diffplug.spotless:spotless-maven-plugin`）做统一格式化，用法对标 biome cli：

| 目的 | 命令 | biome 对应 |
|---|---|---|
| 格式化全部代码 | `mvn spotless:apply` | `biome format -w` |
| 只校验、不改文件 | `mvn spotless:check` | `biome check` |
| 校验并跑完所有模块 | `mvn spotless:check -fae` | — |
| 只处理单个/一类文件 | `mvn -pl nabu-web spotless:apply "-DspotlessFiles=.*JwtAuthInterceptor\.java"` | `biome format -w <path>` |

配置只在根 `pom.xml` 的 `<build><plugins>` 里声明一次，14 个模块自动继承，
每个模块的 `src/main/java` 与 `src/test/java` 都在范围内（当前共 134 个 `.java`）。
版本号集中在根 pom 的 `<properties>`：`spotless.version` / `palantir-java-format.version`。

## 风格参数

- 格式化引擎：`palantir-java-format`，`<style>PALANTIR</style>` —— 4 空格缩进 / 120 列，
  与仓库原有风格一致（`GOOGLE` 是 2 空格 / 100 列，`AOSP` 是 4 空格 / 100 列）。
- `formatJavadoc=false`：不重排 Javadoc 与块注释。本项目注释以中文为主，重排会产生大量无意义 diff。
- `removeUnusedImports` + `trimTrailingWhitespace` + `endWithNewline`。
- 全局 `<lineEndings>UNIX</lineEndings>`：固定 LF，不受 `.gitattributes` 与操作系统默认换行影响。
- 导入顺序：由 palantir 统一按 ASCII 排序（`java.*` 不再单独放最后，这是与 IntelliJ 默认顺序唯一的差别）。

## 临时豁免

需要保留特定排版的代码段，用开关包住（配置里已启用 `toggleOffOn`）：

```java
// @formatter:off
private static final Map<String, String> ALIGN_TABLE = Map.of(
        "a", "1",   "b", "2");
// @formatter:on
```

## 覆盖范围与扩展

当前只管 Java。以下格式**故意没有开**，原因写在后面：

- YAML（`application.yml` / `deploy/compose.*.yml`）：Spotless 的 YAML 格式化基于 Jackson 重写整个文档，
  会丢注释、可能改变折行，对带大量说明注释的 compose 文件不安全。
- SQL（Flyway migration）：Spotless 的 SQL 步骤基于 Eclipse Datatools，需要额外拉一个大依赖，
  且对 MySQL 方言的重排结果不稳定；migration 一旦上生产就不该再改动，包括排版。
- Markdown：格式化会重排表格，README 里的端口表 diff 会很吵。

需要时在根 pom 的 `<configuration>` 内追加对应块即可，例如 `<sql>`、`<markdown>`、`<yaml>`。

## CI 门禁（默认关闭）

格式化目前是显式动作，没有绑定到生命周期，因此 `mvn package` 不会因为格式问题而失败。
要变成强制门禁，打开根 pom 里那段被注释掉的 `<execution>`（绑到 `validate`），
CI 里就可以直接用：

```bash
mvn -B spotless:check        # 不通过就 fail build
mvn -B spotless:apply        # 本地修复
```

## 已验证的环境

- Apache Maven 3.9.16，JDK 25（仓库编译目标仍是 `release=21`）。
  Spotless 3.10.2 + palantir-java-format 2.98.0 在 JDK 25 下可直接运行，
  **不需要**在 `.mvn/jvm.config` 里加 `--add-exports jdk.compiler/...`（用老版本 google-java-format 才需要）。
- 首次全量执行 `mvn spotless:apply` 改了 71 个 `.java`；随后
  `mvn spotless:check` 14 个模块全部通过，`mvn -B -DskipTests package` BUILD SUCCESS。
