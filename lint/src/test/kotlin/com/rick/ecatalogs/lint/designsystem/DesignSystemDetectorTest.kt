package com.rick.ecatalogs.lint.designsystem

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.rick.ecatalogs.lint.designsystem.DesignSystemDetector.Companion.ISSUE
import com.rick.ecatalogs.lint.designsystem.DesignSystemDetector.Companion.METHOD_NAMES
import org.junit.Test

class DesignSystemDetectorTest {

    @Test
    fun `detect replacements of Composable`() {
        lint()
            .issues(ISSUE)
            .allowMissingSdk()
            .files(
                COMPOSABLE_STUB,
                STUBS,
                @Suppress("LintImplTrimIndent")
                kotlin(
                    """
                    |import androidx.compose.runtime.Composable
                    |
                    |@Composable
                    |fun App() {
                    ${METHOD_NAMES.keys.joinToString("\n") { "|    $it()" }}
                    |}
                    """.trimMargin(),
                ).indented(),
            )
            .run()
            .expect(
                """
                src/test.kt:5: Error: Using MaterialTheme instead of EcsTheme [DesignSystem]
                    MaterialTheme()
                    ~~~~~~~~~~~~~~~
                src/test.kt:6: Error: Using darkColorScheme instead of EcsTheme [DesignSystem]
                    darkColorScheme()
                    ~~~~~~~~~~~~~~~~~
                src/test.kt:7: Error: Using lightColorScheme instead of EcsTheme [DesignSystem]
                    lightColorScheme()
                    ~~~~~~~~~~~~~~~~~~
                3 errors, 0 warnings
                """.trimIndent(),
            )
    }

    @Test
    fun `allows MaterialTheme inside the EcsTheme definition`() {
        lint()
            .issues(ISSUE)
            .allowMissingSdk()
            .files(
                COMPOSABLE_STUB,
                STUBS,
                kotlin(
                    "src/com/rick/ecatalogs/EcsTheme.kt",
                    """
                    import androidx.compose.runtime.Composable

                    @Composable
                    fun EcsTheme() {
                        MaterialTheme()
                    }
                    """,
                ).indented(),
            )
            .run()
            .expectClean()
    }

    @Test
    fun `detect platform colors`() {
        lint()
            .issues(ISSUE)
            .allowMissingSdk()
            .files(
                ANDROID_R_STUB,
                kotlin(
                    """
                    fun color() = android.R.color.holo_blue_dark
                    """,
                ).indented(),
            )
            .run()
            .expect(
                """
                src/test.kt:1: Error: Using android.R.color.holo_blue_dark instead of MaterialTheme.colorScheme.primary [DesignSystem]
                fun color() = android.R.color.holo_blue_dark
                              ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent(),
            )
    }

    private companion object {

        private val COMPOSABLE_STUB: TestFile = kotlin(
            """
            package androidx.compose.runtime
            annotation class Composable
            """.trimIndent(),
        ).indented()

        private val STUBS: TestFile = kotlin(
            "src/Stubs.kt",
            """
            |import androidx.compose.runtime.Composable
            |
            ${METHOD_NAMES.keys.joinToString("\n") { "|@Composable fun $it() = {}" }}
            |
            """.trimMargin(),
        ).indented()

        private val ANDROID_R_STUB: TestFile = kotlin(
            "src/android/R.kt",
            """
            package android
            object R {
                object color {
                    const val holo_blue_dark = 1
                    const val darker_gray = 2
                }
            }
            """.trimIndent(),
        ).indented()
    }
}
