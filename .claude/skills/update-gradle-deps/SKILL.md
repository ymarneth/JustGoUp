---
name: gradle-update-deps
description: Update Gradle backend dependencies — list outdated, update Gradle wrapper and JDK, classify and apply safe updates, verify the build, report results. Use when asked to update, upgrade, bump, or patch dependencies in a Gradle/JVM project.
user-invocable: true
---

# Update Gradle dependencies

Keep a Gradle backend's dependencies current: classify outdated packages, apply safe updates automatically, gate framework/major bumps behind a single grouped question backed by web research, update the Gradle wrapper and JDK target, and verify the build stays green.

## Scope

Only touches Gradle build files — plugins block, dependency version strings, and `gradle/wrapper/`. Detect which files those are before making any changes:

1. If `./project.md` lists the build files that carry explicit dependency versions, use that list.
2. Otherwise, treat the root `build.gradle.kts` / `build.gradle` as the file that owns the Gradle/Kotlin/framework plugin versions, and locate any subproject build files (`settings.gradle.kts` lists the modules) that declare direct dependencies with explicit versions.
3. Detect the DSL flavor per file (`build.gradle.kts` = Kotlin DSL, `build.gradle` = Groovy DSL) and adjust syntax examples below accordingly — the steps in this skill are written in Kotlin DSL; translate mechanically for Groovy.

The rest of this document calls the root build file `<root-build-file>` and any module build files with explicit dependency versions `<module-build-files>`.

## Platform note — which wrapper command to use

| Shell | Command prefix |
|-------|---------------|
| Git Bash / Linux / macOS | `./gradlew` |
| PowerShell / cmd.exe | `.\gradlew.bat` |

The Bash tool in Agentic Coding Tool normally uses Git Bash, so `./gradlew` works there. PowerShell requires `.\gradlew.bat`. Detect which shell is active and use the right prefix consistently throughout this skill. When in doubt, use the Bash tool with `./gradlew`.

---

## Steps

### 1 — Ensure `dependencyUpdates` plugin is available

Check whether `com.github.ben-manes.versions` is already in `<root-build-file>`'s plugins block.

**If missing**, add it to the `plugins { ... }` block:

```kotlin
id("com.github.ben-manes.versions") version "0.54.0"
```

Also add a filter block (below the plugins block, at root level) to reject unstable/pre-release versions from the report:

```kotlin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    rejectVersionIf {
        val version = candidate.version.lowercase()
        listOf("alpha", "beta", "rc", "cr", "m", "preview", "snapshot").any { version.contains(it) }
    }
}
```

If the plugin was added, **commit it first** before proceeding (this is a tooling-only change):
```
git add <root-build-file>
git commit -m "Add ben-manes versions plugin for dependency update checks"
```

### 2 — List outdated dependencies

```
./gradlew dependencyUpdates
```

Print the full output. The report lists each dependency with its current version, latest stable version, and whether an update is available. Show it to the user before making any changes.

### 3 — Check latest Gradle wrapper version

Do a **web search** for "latest Gradle version" to get the current release number (do not rely on training data — Gradle releases frequently).

Compare with the version in `gradle/wrapper/gradle-wrapper.properties` (`distributionUrl`).

If an update is available, note the current → latest version for step 5.

### 4 — Check latest JDK LTS version

Do a **web search** for "latest Java JDK LTS version" to confirm the current LTS.

Compare with `sourceCompatibility` / `targetCompatibility` / `JvmTarget.JVM_XX` in `<root-build-file>`.

If the project is already on the latest LTS, note that no JDK update is needed. If an update is available, note it for step 5 with a recommendation.

### 5 — Classify dependency updates

Split findings from step 2 into four groups:

**Group A — Patch updates (apply automatically)**
Any dependency where current → latest is a patch version bump (same major and minor).
Example: `1.1.6 → 1.1.7`

**Group B — Minor updates, non-framework (apply automatically)**
Any minor-only update (same major) for a dependency that is not the project's primary framework.
Example: `io.github.openhtmltopdf:openhtmltopdf-pdfbox 1.1.37 → 1.2.0`

**Group C — Primary framework minor update (ask + recommendation)**
If `./project.md` names a primary framework (e.g. Spring Boot, Quarkus, Micronaut) with its own BOM/platform, treat a minor bump of that framework (e.g. `4.0.x → 4.1.x`) with extra scrutiny — it carries a higher risk of autoconfiguration changes, property renames, and behavior differences than a typical library minor update. If `project.md` does not name a primary framework, skip this group and classify framework-adjacent minor bumps as Group B.

Before asking the user, do a **web search** for:
- `"<framework> <old> to <new> migration guide"`
- `"<framework> <new> release notes breaking changes"`

Summarize the key findings: renamed properties, removed autoconfiguration, changed defaults, required manual steps. Then **pause and ask the user**:
- State the current → target version
- List the relevant breaking changes and required migration steps found
- Give a recommendation: **Recommend: yes** or **Recommend: no / wait**
- Mention that updating the framework's version also updates **all** BOM-managed transitive dependencies automatically. These do **not** need separate version entries.

> **BOM-managed transitive check**: Before classifying any dependency as Group D (major), verify whether it has an explicit version string in `<root-build-file>` or `<module-build-files>`. If it has **no explicit version**, it is platform/BOM-managed and will follow the framework version automatically — do NOT treat it as an independent Group D update. The `dependencyUpdates` task reports the Maven Central latest regardless of BOM management and can mislead you here. Check `./project.md` for a project-specific list of known BOM-managed dependencies; if none is listed, infer BOM management from the absence of an explicit version string.

**Group D — Major updates (ask + recommendation)**
Any dependency where the major version would increase **and** the dependency has an explicit version string in the build files — e.g. the language plugin (Kotlin, Java toolchain), the primary framework, or any other plugin/direct dependency with an explicit version.

Before asking the user, do a **web search** for each Group D package:
- `"<package> <old-version> to <new-version> migration guide"`
- `"<package> <new-version> breaking changes"`

Summarize migration effort per package. Then **pause and ask the user** with a grouped recommendation (one question covers all Group D items together). For each package state the version jump, key breaking changes found, likely migration effort, and a clear **Recommend: yes / no / wait**.

**Language/framework coordination**: If the project's language plugin (e.g. Kotlin) and its primary framework both have updates, always coordinate them — search for `"<framework> <version> supported <language> version"` to confirm the valid range before recommending either.

If the user says no or skip for any group, note them in the report as skipped.

---

### 6 — Apply all approved updates

Apply changes in this order:

#### 6a — Direct dependency versions

For each approved group A / B / D dependency:
- Edit the version string in `<root-build-file>` or the relevant `<module-build-files>` directly (use Edit tool, not sed).
- Framework and language plugin version strings typically live in the root plugins block; direct dependency versions with no platform/BOM management typically live in the module build file that declares them.

#### 6b — Gradle wrapper update (if a newer version was found in step 3)

Run the wrapper task **twice** — this is required: the first run updates `gradle-wrapper.properties`; the second run uses the newly downloaded Gradle to regenerate `gradlew`, `gradlew.bat`, and `gradle-wrapper.jar`.

```bash
./gradlew wrapper --gradle-version <VERSION>
./gradlew wrapper --gradle-version <VERSION>
```

Verify `gradle/wrapper/gradle-wrapper.properties` now contains the new version.

#### 6c — JDK version update (if approved in step 4)

Edit `<root-build-file>`:
- `sourceCompatibility = "<N>"`
- `targetCompatibility = "<N>"`
- `JvmTarget.JVM_<N>`

#### 6d — Commit version bumps

```
git add <root-build-file> <module-build-files> gradle/wrapper/
git commit -m "Update Gradle wrapper to <VERSION>, patch/minor dependency updates"
```

If a framework minor or major update was also applied, use a separate commit:
```
git add <root-build-file>
git commit -m "Upgrade <framework> <old> → <new>"
```

#### 6e — Apply vendor migration guide steps

For each approved Group C or Group D update that had a migration guide (found in step 5):

1. **Grep the project** for each breaking change pattern identified in the migration guide — check config files (e.g. `application.yml`/`.properties`), source files, and build files.
2. **Apply only the changes that actually affect this project** — do not blindly apply every item from the guide, only those where the grep confirmed the affected pattern exists.
3. Typical changes include: renamed properties in config files, removed deprecated API usages in source, updated import statements, replaced removed classes.
4. After applying, **commit migration changes separately**:

```
git add -A
git commit -m "Apply <framework> <old>→<new> migration guide steps"
```

If no breaking changes from the migration guide affect the project, skip this step and note "no migration steps required" in the report.

---

### 7 — Verify nothing is broken

Run all checks in order. **If any step fails, stop immediately, report the failure clearly, and ask the user how to proceed before committing anything further.**

#### 7a — Full build with deprecation warnings visible

```
./gradlew clean build --warning-mode all
```

Run any additional verify tasks listed in `./project.md` (e.g. docs generation, integration test tasks) alongside it.

This runs compilation, tests, and any configured verification tasks. The `--warning-mode all` flag surfaces deprecation warnings from Gradle APIs or plugin usages that may indicate future breakage.

If the build fails: identify which dependency update likely caused it (check compiler errors for changed APIs, missing classes, renamed properties).

#### 7b — Tests only (if step 7a fails mid-build)

If `clean build` fails but you need to isolate whether it's a compile or test failure:

```
./gradlew test
```

#### 7c — Failure handling

If step 7a or 7b fails:

1. **Identify** which dependency update likely caused the failure — check compiler errors for changed APIs, removed classes, renamed properties, or incompatible versions.
2. **Report clearly** to the user: which package, what the error is, and why it is likely the cause.
3. **Ask the user** to choose one of:
    - **Revert** the offending update (restore the previous version in the build file)
    - **Fix** the issue (update source code to adapt to the new API)
    - **Proceed anyway** (leave the build broken and continue to the report)

Do not commit or continue to step 8 until the user has chosen and the chosen action is complete.

#### 7d — Deprecation warnings

After a successful build, scan the `--warning-mode all` output for deprecation warnings. If there are warnings linked to a dependency that was just updated, report them explicitly in the final report — they are not blockers but indicate future work.

---

### 8 — Squash commits

After all changes are committed and the build is green, squash all commits made during this session into one (or two, if a framework major was also applied separately).

Count the commits made during this session and run:

```bash
git reset --soft HEAD~<N>
git commit -m "Patch day -- Gradle <version>, <framework> <old> -> <new>, <language> <old> -> <new>"
```

Adapt the message to only mention what actually changed. Examples:
- `Patch day -- Gradle 9.6.0, Spring Boot 4.0.4 -> 4.1.0, Kotlin 2.3.20 -> 2.4.0`
- `Patch day -- Gradle 9.7.0, patch dependency updates`

**Exception:** If a framework *major* update was applied, keep it as a separate commit so it can be reverted independently:
1. Squash everything except the framework major commit
2. Leave the framework major commit as-is (or squash its migration sub-steps into it)

---

### 9 — Report

Print a Markdown summary:

```
## Patch Day Report — Gradle Backend

### Gradle wrapper
- <old version> → <new version>  (or "already at latest: <version>")

### JDK target
- <old JVM target> → <new JVM target>  (or "already at latest LTS: Java <N>")

### Updated dependencies
| Dependency | Was | Now | Group |
|-----------|-----|-----|-------|
| <framework> | ... | ... | C (minor, approved) |
| <language plugin> | ... | ... | A (patch) |
| ... | | | |

### Skipped dependencies
| Dependency | Was | Latest | Reason |
|-----------|-----|--------|--------|
| ... | | | Major — user deferred |

### Deprecation warnings
- <warning text> — in: <file:line>  (or "none")

### Commit
- <short hash> <squashed commit message>

### Next steps
<Any manual migration steps, e.g. "Review <framework> migration guide for property renames", "Check <language> migration guide">
<If the JDK major was updated: also check ./project.md for CI image naming conventions that need bumping — flag the specific files/lines if project.md documents them, otherwise ask the user where the JDK version is pinned for CI.>
```

---

## Rules

- Never run `./gradlew wrapper --gradle-version` only once — always run it **twice** to update all wrapper files (properties + JAR + scripts).
- Only modify source files or config files (e.g. `application.yml`/`.properties`) when step 6e explicitly requires it as part of applying a migration guide. All other changes are restricted to `<root-build-file>`, `<module-build-files>`, and `gradle/wrapper/`.
- Never modify database migration files (e.g. Flyway/Liquibase) or schema-generation settings (e.g. `ddl-auto`) as part of a dependency update.
- Always use the latest stable Gradle version (no -rc, -milestone, -snapshot suffixes).
- If the project uses a language plugin with its own release cadence (e.g. Kotlin), keep it compatible with the active primary-framework version — check the framework's release notes if both have updates.
- Do not commit config files that may contain secrets (`application.yml`/`.properties` with credentials, `.env` files) unless the change is a config-key rename required by step 6e, and even then only the key name/structure — never commit secret values.

## Project-specific rules

If `./project.md` exists in this directory, load it and apply its rules before acting — e.g. the root/module build files that carry explicit dependency versions, the DSL flavor if not auto-detectable, the primary framework name and its known BOM-managed dependency list, additional verify tasks to run in step 7a, and where the JDK/CI image version is pinned so a JDK major bump can be flagged precisely (file paths/line numbers, image naming convention).
