import com.rick.data.translation.DemoTranslationAssetManager
import java.io.File
import java.io.InputStream
import java.util.Properties

internal object JvmUnitTestTranslationDemoAssetManager : DemoTranslationAssetManager {

    private val config =
        requireNotNull(javaClass.getResource("com/android/tools/test_config.properties")) {
            """
            Missing Android resources properties file.
            Did you forget to enable the feature in the gradle build file?
            android.testOptions.unitTests.isIncludeAndroidResources = true
            """.trimIndent()
        }
    private val properties = Properties().apply { config.openStream().use(::load) }
    private val assets = File(properties["android_merged_assets"].toString())

    override fun open(filename: String): InputStream {
        return File(assets, filename).inputStream()
    }

}