plugins {
    alias(libs.plugins.ecatalogs.android.application)
    alias(libs.plugins.ecatalogs.android.application.jacoco)
    alias(libs.plugins.ecatalogs.android.navigation)
    id("jacoco")
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.baselineprofile)
}

android {
    defaultConfig {
        applicationId = "com.rick.moviecatalog"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.rick.data.testing.EcsTestRunner"

    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        debug {
            applicationIdSuffix = EcsBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            applicationIdSuffix = EcsBuildType.RELEASE.applicationIdSuffix
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("debug")
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.rick.moviecatalog"
}

dependencies {

    implementation(projects.core)
    implementation(projects.data.datastore)
    implementation(projects.data.datastoreProto)

    implementation(projects.movie.dataMovie)
    implementation(projects.movie.screenMovie)
    implementation(projects.book.dataBook)
    implementation(projects.book.screenBook)
    implementation(projects.anime.dataAnime)
    implementation(projects.anime.screenAnime)
    implementation(projects.authentication.authData)
    implementation(projects.authentication.authScreen)

    implementation(libs.androidx.core)
    implementation(libs.androidx.app.compat)
    implementation(libs.android.material)
    implementation(libs.androidx.splash.screen)
    kspTest(libs.hilt.compiler)

//    testImplementation(projects.data.t)
//    testImplementation(projects.data.testing)
//    testImplementation(libs.accompanist.testharness)
    testImplementation(libs.hilt.testing)
//    testImplementation(libs.work.testing)

//    testDemoImplementation(libs.robolectric)
//    testDemoImplementation(libs.roborazzi)
//    testDemoImplementation(projects.core.screenshotTesting)

//    androidTestImplementation(projects.data.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.hilt.testing)

//    baselineProfile(projects.benchmarks)
}