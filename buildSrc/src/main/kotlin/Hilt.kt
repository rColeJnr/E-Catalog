object Hilt {
    private const val hiltVersion = "2.42"
    const val android = "com.google.dagger:hilt-android:$hiltVersion"
    const val compiler = "com.google.dagger:hilt-compiler:$hiltVersion"
    const val navigation = "androidx.hilt:hilt-navigation-compose:1.0.0"

    const val hiltPlugin = "com.google.dagger:hilt-android-gradle-plugin:${hiltVersion}"

    const val hiltAndroidTesting = "com.google.dagger:hilt-android-testing:${hiltVersion}"
}