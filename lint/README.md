# :lint

Custom lint checks for E-Catalogs (modelled on Now in Android's `:lint` module).

| Issue | Severity | What it flags |
|---|---|---|
| `DesignSystem` | Error | Compose Material APIs that have an E-Catalogs replacement, e.g. `MaterialTheme(...)` instead of `EcsTheme { }` |
| `TestMethodPrefix` | Warning | Test methods starting with `test` |
| `TestMethodFormat` | Warning | Instrumented tests not named `given_when_then` / `when_then` |

The checks are published by `:data:ui_components:common` (`lintPublish`), so every module that
uses the shared Compose components gets them, and run with `./gradlew lint` / `lintDemoDebug`.
Run this module's own tests with `./gradlew :lint:test`.
