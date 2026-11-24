plugins {
    alias(libs.plugins.ecatalogs.android.library)
}

android {
    namespace = "com.rick.ecatalogs.data.datastore.test"
}

dependencies {
    api(libs.androidx.datastore.core)
}
