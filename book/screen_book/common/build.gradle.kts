plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {
    namespace = "com.rick.book.screen_book.common"
}

dependencies {
    implementation(projects.book.dataBook.domain)
    implementation(projects.data.analytics)
    implementation(projects.data.translation)
}