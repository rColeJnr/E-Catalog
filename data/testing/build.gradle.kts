plugins {
    alias(libs.plugins.ecatalogs.android.library)
}

android {
    namespace = "com.rick.ecatalogs.data.testing"
}

dependencies {
    api(libs.junit)
    api(libs.kotlin.test)
    api(libs.kotlinx.coroutines.test)
    api(projects.data.analytics)
    api(projects.data.notifications)
    api(projects.data.util)
    api(projects.settings.dataSettings.data)
    api(projects.settings.dataSettings.model)
}
