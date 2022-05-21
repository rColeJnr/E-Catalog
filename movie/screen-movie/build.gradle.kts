apply {
    from( "$rootDir/android-library.gradle")
}

dependencies {

    "implementation" (project(":core"))
    "implementation" (project(":movie:domain-movie"))

}