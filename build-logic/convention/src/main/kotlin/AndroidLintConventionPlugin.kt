import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") -> configure<ApplicationExtension> {
                    lint(
                        Lint::ricardo
                    )
                }

                pluginManager.hasPlugin("com.android.library") -> configure<LibraryExtension> {
                    lint(
                        Lint::ricardo
                    )
                }

                else -> {
                    pluginManager.apply("com.android.lint")
                    configure<Lint>(Lint::ricardo)
                }
            }
        }
    }
}

// configure()
private fun Lint.ricardo() {
    xmlReport = true
    checkDependencies = true
}