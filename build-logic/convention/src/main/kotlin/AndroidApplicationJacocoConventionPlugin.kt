import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.dsl.ApplicationExtension
import com.rick.ecatalogs.buildlogic.configureJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationJacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.gradle.jacoco")
            }
            configureJacoco(
                commonExtension = extensions.getByType<ApplicationExtension>(),
                androidComponentsExtension = extensions.getByType<ApplicationAndroidComponentsExtension>(),
            )
        }
    }
}