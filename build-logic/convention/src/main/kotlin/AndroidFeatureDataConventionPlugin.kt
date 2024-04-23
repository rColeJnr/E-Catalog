import com.rick.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureDataConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("ecatalogs.android.library")
                apply("ecatalogs.android.hilt")
                apply("ecatalogs.android.room")
            }


            dependencies {
                add("implementation", project(":data:common"))
                add("implementation", libs.findLibrary("square-retrofit").get())
                add("implementation", libs.findLibrary("square-converter-gson").get())
                add("implementation", libs.findLibrary("square-okhttp").get())
                add("implementation", libs.findLibrary("square-logging-interceptor").get())
                add("implementation", libs.findLibrary("paging-runtime").get())
                add("implementation", libs.findLibrary("paging-common").get())
                add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
            }
        }
    }

}