// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
//    dependencies {
////        classpath(Nav.navPlugin)
////        classpath Hilt.hiltPlugin
////        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
////        classpath("com.google.gms:google-services:4.3.3")
//    }
    repositories {
        google()
        mavenCentral()

//        Android Build Server

    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.dependencyGuard) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false // i use parcelize,
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.module.graph) apply false
}

// Task to print all the module paths in the project e.g. :core:data
// Used by module graph generator script
tasks.register("printModulePaths") {
    subprojects {
        if (subprojects.size == 0) {
            println(this.path)
        }
    }
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))