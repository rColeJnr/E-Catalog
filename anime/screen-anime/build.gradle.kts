apply {
    from( "$rootDir/android-library.gradle")
}
apply(plugin = "org.jetbrains.kotlin.android")

dependencies {

    "implementation"(project(Modules.core))
    "implementation"(project(Modules.animeData))

}