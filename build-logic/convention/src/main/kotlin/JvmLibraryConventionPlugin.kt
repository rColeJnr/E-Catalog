import com.rick.ecatalogs.buildlogic.allowModulesWithoutTests
import com.rick.ecatalogs.buildlogic.configureKotlinJvm
import com.rick.ecatalogs.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            allowModulesWithoutTests()
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("ecatalogs.android.lint")
            }
            configureKotlinJvm()
            dependencies {
                "testImplementation"(libs.findLibrary("kotlin.test").get())
                "testImplementation"(libs.findLibrary("junit").get())
            }
        }
    }
}