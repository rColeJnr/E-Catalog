apply {
    from( "$rootDir/android-library.gradle")
}

dependencies {

    "implementation" (project(Modules.core))
    "implementation"(project(Modules.bookData))

}