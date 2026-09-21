# Repository Guidelines

Nabu is a Java 21 + Spring Boot 3.5 + Dubbo 3.3 microservice forum monorepo (Maven, `com.xfy.nabu`, 14 modules). This guide complements `README.md`, `docs/code-style.md`, and `docs/canal-setup.md`.

## Project Structure

- `nabu-common/` — shared Result type, exceptions, TTL context, constants (library jar)
- `nabu-api/` — Dubbo service contracts + DTOs shared by providers/consumers (library jar)
- `nabu-<domain>-service/` — business services (auth, user, forum, social, notify, search, file, moderation, stat, task, admin)
- `nabu-web/` — the only user-facing HTTP/BFF entry; internal calls use Dubbo Triple
- `deploy/` — Docker Compose files, `.env`, per-component config, `scripts/`; `docs/` — guides
- Per module: `src/main/java`, `src/main/resources/application.yml`, `db/migration/V<n>__<desc>.sql` (Flyway)

## Build & Run

```bash
mvn -B -DskipTests package                                  # full build
mvn -B -pl nabu-user-service -am package -DskipTests        # one module + deps
cd deploy && ./scripts/up.sh obs                            # start infra+middleware (Nacos first!)
mvn -pl nabu-user-service spring-boot:run                   # run a service on the host
./scripts/logs.sh nabu-user-service | ./scripts/down.sh     # container logs / teardown
```

Never run `*.jar.original` (pre-repackage thin jar). Service ports are listed in `README.md`; changing versions requires editing root `pom.xml` `<properties>` only.

## Coding Style

- Spotless + palantir-java-format: 4-space indent, 120 columns, LF endings.
- `mvn spotless:apply` before committing; `mvn spotless:check` to verify (per-module: `-pl nabu-web "-DspotlessFiles=.*JwtAuthInterceptor\.java"`).
- Do not reflow Chinese comments/Javadoc (`formatJavadoc=false`); wrap exceptions in `// @formatter:off/on`.
- YAML, SQL, Markdown are intentionally unformatted — do not "clean up" compose/migration files.
- Comments are written in Chinese; mark intentional simplifications with what production should do.

## Testing

JUnit 5 + Mockito + Testcontainers 1.20.6 are managed in the root pom; the suite is currently minimal. Add tests under each module's `src/test/java` named `*Test.java`, run with `mvn test` (module-scoped: `mvn -pl <module> test`). Docker-based integration tests must not assume host port 6379/8443 (offset to 6380/18443 locally).

## Commit & Pull Request

- Conventional Commits with Chinese descriptions: `fix(build): 修复 mvn compile 失败...`, `chore: ...`.
- Commit each completed feature point (`功能点`) as its own commit once it passes its gate (build/tests/`spotless:check`) — do not batch it with unrelated or unfinished work.
- A local commit never implies push/MR/tag/release; those remain separate, explicitly confirmed milestones.
- Branch from `main` (agent branches use the `codex/` prefix); PRs state affected modules, linked behavior changes, and verification commands run (build/test/spotless). Include screenshots or API samples only for `nabu-web` user-facing changes.

## Security & Configuration

Local DB credentials (`nabu`/`nabu123456`) and `.env` values are dev-only — never reuse in production or commit real secrets. Migrations are append-only once shared.
