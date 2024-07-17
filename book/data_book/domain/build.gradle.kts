plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.rick.book.data_book.domain"
}

dependencies {
    api(projects.book.dataBook.data)
    api(projects.book.dataBook.model)
    api(projects.data.translation)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

//    testImplementation(projects.core.testing)
}