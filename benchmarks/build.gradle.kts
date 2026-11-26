import com.rick.ecatalogs.buildlogic.configureFlavors

plugins {
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.ecatalogs.android.test)
}

android {
    namespace = "com.rick.ecatalogs.benchmarks"

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    // Same flavors as the app: Baseline Profiles are generated on prod (what ships), while
    // benchmarks can also run on demo for stable data.
    configureFlavors(this) { flavor ->
        buildConfigField(
            "String",
            "APP_FLAVOR_SUFFIX",
            "\"${flavor.applicationIdSuffix ?: ""}\"",
        )
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

baselineProfile {
    // NiA uses a Gradle Managed Device; that downloads a ~1 GB system image, so this runs on
    // a connected phone or emulator (API 28+, signed in, so the app opens on Trending Series).
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.ext)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.uiautomator)
}
