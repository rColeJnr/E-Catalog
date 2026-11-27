import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.dsl.LibraryExtension
import com.rick.ecatalogs.buildlogic.configureJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryJacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.gradle.jacoco")
            }
            configureJacoco(
                commonExtension = extensions.getByType<LibraryExtension>(),
                androidComponentsExtension = extensions.getByType<LibraryAndroidComponentsExtension>(),
            )
        }
    }
}