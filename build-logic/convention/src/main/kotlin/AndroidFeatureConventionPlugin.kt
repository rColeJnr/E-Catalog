import com.android.build.api.dsl.LibraryExtension
import com.rick.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("ecatalogs.android.library")
                apply("ecatalogs.android.hilt")
                apply("ecatalogs.android.navigation")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner =
                        "com.rick.data.testing.EcsTestRunner"
                }
                ndkVersion = "25.1.8937393"
                testOptions.animationsDisabled = true
            }

            dependencies {
                add("implementation", libs.findLibrary("kotlinx.coroutines").get())
                add("implementation", libs.findLibrary("kotlinx.coroutines.core").get())
                add("implementation", libs.findLibrary("androidx.swiperefreshlayout").get())
                add("implementation", libs.findLibrary("android.material").get())
                add("implementation", libs.findLibrary("lifecycle.viewmodel").get())
                add("implementation", libs.findLibrary("bumptech.glide").get())
            }
        }
    }
}