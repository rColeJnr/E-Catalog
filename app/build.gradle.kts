import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.ecatalogs.android.application)
    alias(libs.plugins.ecatalogs.android.application.flavors)
    alias(libs.plugins.ecatalogs.android.application.firebase)
    alias(libs.plugins.ecatalogs.android.application.jacoco)
    alias(libs.plugins.ecatalogs.android.navigation)
    id("jacoco")
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.compose)
    alias(libs.plugins.google.osslicenses)
}

android {
    defaultConfig {
        applicationId = "com.rick.moviecatalog"
        // Play releases set both from the git tag (.github/workflows/release.yml: v2.3.1 → 20301 /
        // "2.3.1"); local builds keep the last hand-set values.
        versionCode = providers.gradleProperty("ecsVersionCode").map(String::toInt).getOrElse(10)
        versionName = providers.gradleProperty("ecsVersionName").getOrElse("2.2")

        testInstrumentationRunner = "com.rick.ecatalogs.data.testing.EcsTestRunner"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    // Play upload key, from local.properties (never committed): releaseStoreFile (path to the
    // keystore), releaseStorePassword, releaseKeyAlias, releaseKeyPassword. Without them release
    // builds are signed with the debug key, so a fresh clone can still build and run them.
    val localProperties = gradleLocalProperties(rootDir, providers)
    val releaseStoreFile = localProperties.getProperty("releaseStoreFile")
    if (releaseStoreFile != null) {
        signingConfigs.create("release") {
            storeFile = file(releaseStoreFile)
            storePassword = localProperties.getProperty("releaseStorePassword")
            keyAlias = localProperties.getProperty("releaseKeyAlias")
            keyPassword = localProperties.getProperty("releaseKeyPassword")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = EcsBuildType.DEBUG.applicationIdSuffix
        }
        release {
            applicationIdSuffix = EcsBuildType.RELEASE.applicationIdSuffix
            // R8: shrinks, optimizes and obfuscates (see proguard-rules.pro). -PminifyWithR8=false
            // turns it off to tell an R8 problem from an app bug, as in Now in Android.
            isMinifyEnabled = providers.gradleProperty("minifyWithR8")
                .map(String::toBooleanStrict).getOrElse(true)
            isShrinkResources = isMinifyEnabled
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
        jniLibs {
            // 16 KB page size: androidx.graphics:graphics-path (via Compose ui-graphics) ships a
            // native lib whose RELRO segment isn't 16 KB aligned in any release up to 1.1.0.
            // AndroidX only loads it below API 34 (newer devices use the platform PathIterator),
            // and all 16 KB-page devices run Android 15+, so it's excluded. Remove this once a
            // fixed graphics-path release is available.
            excludes.add("**/libandroidx.graphics.path.so")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.rick.ecatalogs"
}

dependencies {

    implementation(projects.movie.screenMovie.articleCatalog)
    implementation(projects.movie.screenMovie.articleFavorite)
    implementation(projects.movie.screenMovie.articleSearch)
    implementation(projects.movie.screenMovie.trendingMovieCatalog)
    implementation(projects.movie.screenMovie.trendingMovieDetails)
    implementation(projects.movie.screenMovie.personDetails)
    implementation(projects.movie.screenMovie.trendingMovieFavorite)
    implementation(projects.movie.screenMovie.trendingMovieSearch)
    implementation(projects.movie.screenMovie.trendingSeriesCatalog)
    implementation(projects.movie.screenMovie.trendingSeriesFavorite)
    implementation(projects.movie.screenMovie.trendingSeriesDetails)
    implementation(projects.movie.screenMovie.trendingSeriesSearch)
    implementation(projects.movie.screenMovie.common)
    implementation(projects.book.screenBook.common)
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

    implementation(projects.game.screenGame.gameCatalog)
    implementation(projects.game.screenGame.gameDetails)
    implementation(projects.game.screenGame.gameSearch)
    implementation(projects.game.screenGame.gameFavorites)
    implementation(projects.authentication.authScreen)
    implementation(projects.settings.dataSettings.data)
    implementation(projects.settings.screenSettings)
    implementation(projects.data.datastore)
    implementation(projects.data.sync)
    implementation(projects.data.util)
    implementation(projects.data.uiComponents.common)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(projects.data.uiDesign)

    implementation(libs.androidx.core)
    implementation(libs.androidx.app.compat)
    implementation(libs.android.material)
    implementation(libs.androidx.splash.screen)
    implementation(libs.androidx.profileinstaller)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    ksp(libs.hilt.compiler)

    androidTestImplementation(projects.data.testing)
    androidTestImplementation(projects.data.datastore)
    androidTestImplementation(projects.data.datastoreTest)
    androidTestImplementation(projects.authentication.authData)
    androidTestImplementation(projects.data.uiComponents.auth)
    androidTestImplementation(projects.data.trailer)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.androidx.recyclerview)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test)
    kspAndroidTest(libs.hilt.compiler)

    baselineProfile(projects.benchmarks)
}

baselineProfile {
    // Generated on demand (./gradlew :app:generateProdReleaseBaselineProfile), not on every
    // release build: it needs a device.
    automaticGenerationDuringBuild = false

    // Dex Layout Optimizations, from the startup profile
    dexLayoutOptimization = true
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}
