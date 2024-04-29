plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
//    alias(libs.plugins.ecatalogs.android.room)
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk") //Path of android.mk
        }
    }
    namespace = "com.rick.book.screen_book.bestseller_catalog"
}

dependencies {
    implementation(projects.book.dataBook.data)
    implementation(projects.book.dataBook.model)
    implementation(projects.book.screenBook.bestsellerFavorites)
    implementation(projects.book.screenBook.bestsellerSearch)
    implementation(projects.book.screenBook.common)
    implementation(projects.data.uiComponents.common)

    implementation(libs.compose.ui)
}