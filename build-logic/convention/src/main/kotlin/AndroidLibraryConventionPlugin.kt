import com.rick.ecatalogs.buildlogic.allowModulesWithoutTests
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.dsl.LibraryExtension
import com.rick.ecatalogs.buildlogic.configureFlavors
import com.rick.ecatalogs.buildlogic.configureKotlinAndroid
import com.rick.ecatalogs.buildlogic.libs
import com.rick.ecatalogs.buildlogic.disableUnnecessaryAndroidTests
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            allowModulesWithoutTests()
            with(pluginManager) {
                apply("com.android.library")
                apply("ecatalogs.android.lint")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                testOptions.targetSdk = 36
                lint.targetSdk = 36
                testOptions.animationsDisabled = true
                configureFlavors(this)
//                configureGradleManagedDevices(this)
                // The resource prefix is derived from the module name,
                // so resources inside ":core:module1" must be prefixed with "core_module1_"
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                disableUnnecessaryAndroidTests(target)
            }
            dependencies {
                "androidTestImplementation"(libs.findLibrary("kotlin.test").get())
                "testImplementation"(libs.findLibrary("kotlin.test").get())
                "testImplementation"(libs.findLibrary("junit").get())
//                add("implementation", libs.findLibrary("androidx.tracing.ktx").get())
            }
        }
    }
}