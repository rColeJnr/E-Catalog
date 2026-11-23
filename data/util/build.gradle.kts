plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {

    namespace = "com.rick.ecatalogs.data.util"
}

dependencies {
    // SyncManager exposes Flow
    api(libs.kotlinx.coroutines.core)
}