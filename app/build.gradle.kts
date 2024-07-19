plugins {
    alias(libs.plugins.ecatalogs.android.application)
    alias(libs.plugins.ecatalogs.android.application.flavors)
    alias(libs.plugins.ecatalogs.android.application.firebase)
    alias(libs.plugins.ecatalogs.android.application.jacoco)
    alias(libs.plugins.ecatalogs.android.navigation)
    id("jacoco")
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
//    alias(libs.plugins.gms)
//    alias(libs.plugins.baselineprofile)
}

android {
    defaultConfig {
        applicationId = "com.rick.moviecatalog"
        versionCode = 5
        versionName = "1.1"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"/*"com.rick.data.testing.EcsTestRunner"*/
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    buildTypes {
        debug {
            applicationIdSuffix = EcsBuildType.DEBUG.applicationIdSuffix
        }
        release {
            applicationIdSuffix = EcsBuildType.RELEASE.applicationIdSuffix
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.named("debug").get()
            // Ensure Baseline Profile is fresh for release builds.
//            baselineProfile.automaticGenerationDuringBuild = true
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

    implementation(projects.movie.screenMovie.articleCatalog)
    implementation(projects.movie.screenMovie.articleFavorite)
    implementation(projects.movie.screenMovie.articleSearch)
    implementation(projects.movie.screenMovie.trendingMovieCatalog)
    implementation(projects.movie.screenMovie.trendingMovieDetails)
    implementation(projects.movie.screenMovie.trendingMovieFavorite)
    implementation(projects.movie.screenMovie.trendingMovieSearch)
    implementation(projects.movie.screenMovie.trendingSeriesCatalog)
    implementation(projects.movie.screenMovie.trendingSeriesFavorite)
    implementation(projects.movie.screenMovie.trendingSeriesDetails)
    implementation(projects.movie.screenMovie.trendingSeriesSearch)
    implementation(projects.book.screenBook.gutenbergSearch)
    implementation(projects.book.screenBook.gutenbergCatalog)
    implementation(projects.book.screenBook.gutenbergFavorites)
    implementation(projects.book.screenBook.bestsellerSearch)
    implementation(projects.book.screenBook.bestsellerFavorites)
    implementation(projects.book.screenBook.bestsellerCatalog)
    implementation(projects.anime.screenAnime.animeCatalog)
    implementation(projects.anime.screenAnime.animeDetails)
    implementation(projects.anime.screenAnime.animeFavorites)
    implementation(projects.anime.screenAnime.animeSearch)
    implementation(projects.anime.screenAnime.mangaCatalog)
    implementation(projects.anime.screenAnime.mangaDetails)
    implementation(projects.anime.screenAnime.mangaFavorites)
    implementation(projects.anime.screenAnime.mangaSearch)

    implementation(projects.authentication.authScreen)
    implementation(projects.settings.dataSettings.data)
    implementation(projects.settings.screenSettings)
    implementation(projects.data.datastore)

    implementation(libs.androidx.core)
    implementation(libs.androidx.app.compat)
    implementation(libs.android.material)
    implementation(libs.androidx.splash.screen)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    ksp(libs.hilt.compiler)

//    kspTest(libs.hilt.compiler)
//    testImplementation(projects.data.t)
//    testImplementation(projects.data.testing)
//    testImplementation(libs.accompanist.testharness)
//    testImplementation(libs.hilt.testing)
//    testImplementation(libs.work.testing)

//    testDemoImplementation(libs.robolectric)
//    testDemoImplementation(libs.roborazzi)
//    testDemoImplementation(projects.core.screenshotTesting)

//    androidTestImplementation(projects.data.testing)
//    androidTestImplementation(libs.androidx.test.espresso.core)
//    androidTestImplementation(libs.androidx.navigation.testing)
//    androidTestImplementation(libs.hilt.testing)

//    baselineProfile(projects.benchmarks)
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}
