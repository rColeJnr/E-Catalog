package com.rick.ecatalogs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rick.ecatalogs.databinding.ActivityMainBinding
import com.rick.ecatalogs.settings.data_settings.model.SettingsUserData
import com.rick.ecatalogs.settings.data_settings.model.ThemeConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth

    private val viewModel: MainActivityViewModel by viewModels()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        initFirebase()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.myToolbar.setPadding(0, systemBars.top, 0, 0)
            binding.navView.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                //Movie screen1
                com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog.R.id.movie_screen_movie_trending_movie_catalog_trendingmoviefragment,
                com.rick.ecatalogs.movie.screen_movie.trending_series_catalog.R.id.movie_screen_movie_trending_series_catalog_trendingseriesfragment,
                //Book screen
                com.rick.ecatalogs.book.screen_book.gutenberg_catalog.R.id.book_screen_book_gutenberg_catalog_gutenbergcatalogfragment,
                com.rick.ecatalogs.book.screen_book.bestseller_catalog.R.id.book_screen_book_bestseller_catalog_bestsellerfragment,
                //Anime screen
                com.rick.ecatalogs.anime.screen_anime.anime_catalog.R.id.anime_screen_anime_anime_catalog_animecatalogfragment,
                com.rick.ecatalogs.anime.screen_anime.manga_catalog.R.id.anime_screen_anime_manga_catalog_mangacatalogfragment,
                //Movie reviews & settings
                com.rick.ecatalogs.movie.screen_movie.article_catalog.R.id.movie_screen_movie_article_catalog_articlefragment,
                com.rick.ecatalogs.settings.screen_settings.R.id.settings_screen_settings_settingsfragment,
            ),
            binding.drawerLayout
        )
        toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        )

        binding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Activity toolbar or Fragment toolbar
            toolbar.visibility = if (
            //Movie screen
                destination.id == com.rick.ecatalogs.movie.screen_movie.trending_series_search.R.id.movie_screen_movie_trending_series_search_trendingseriessearchfragment
                || destination.id == com.rick.ecatalogs.movie.screen_movie.trending_movie_search.R.id.movie_screen_movie_trending_movie_search_trendingmoviesearchfragment
                || destination.id == com.rick.ecatalogs.movie.screen_movie.trending_movie_details.R.id.movie_screen_movie_trending_movie_details_moviedetailsfragment
                || destination.id == com.rick.ecatalogs.movie.screen_movie.trending_series_details.R.id.movie_screen_movie_trending_series_details_trendingseriesdetailsfragment
                //Book screen
                || destination.id == com.rick.ecatalogs.book.screen_book.gutenberg_search.R.id.book_screen_book_gutenberg_search_searchfragment

                //Anime screen
                || destination.id == com.rick.ecatalogs.anime.screen_anime.anime_search.R.id.anime_screen_anime_anime_search_animesearchfragment
                || destination.id == com.rick.ecatalogs.anime.screen_anime.manga_search.R.id.anime_screen_anime_manga_search_mangasearchfragment
                || destination.id == com.rick.ecatalogs.anime.screen_anime.manga_details.R.id.anime_screen_anime_manga_details_mangadetailsfragment
                || destination.id == com.rick.ecatalogs.anime.screen_anime.anime_details.R.id.anime_screen_anime_anime_details_animedetailsfragment

                //Auth screen
                || destination.id == com.rick.ecatalogs.authentication.auth_screen.R.id.authenticationFragment
            )
                View.GONE
            else
                View.VISIBLE

            //Toolbar title
            binding.toolbarText.text = when (destination.id) {
                //Movie screen
                com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog.R.id.movie_screen_movie_trending_movie_catalog_trendingmoviefragment -> getString(
                    R.string.trending_movies
                )

                com.rick.ecatalogs.movie.screen_movie.article_catalog.R.id.movie_screen_movie_article_catalog_articlefragment -> getString(
                    R.string.movie_reviews
                )

                com.rick.ecatalogs.settings.screen_settings.R.id.settings_screen_settings_settingsfragment -> getString(
                    R.string.settings
                )

                com.rick.ecatalogs.movie.screen_movie.trending_series_catalog.R.id.movie_screen_movie_trending_series_catalog_trendingseriesfragment -> getString(
                    R.string.trending_series
                )

                com.rick.ecatalogs.movie.screen_movie.trending_series_favorite.R.id.movie_screen_movie_trending_series_favorite_trendingseriesfavoritefragment -> getString(
                    R.string.trending_series_favorite
                )

                com.rick.ecatalogs.movie.screen_movie.trending_movie_favorite.R.id.movie_screen_movie_trending_movie_favorite_trenidngmoviefavoritefragment -> getString(
                    R.string.trending_movies_favorite
                )

                //Book screen
                com.rick.ecatalogs.book.screen_book.gutenberg_catalog.R.id.book_screen_book_gutenberg_catalog_gutenbergcatalogfragment -> getString(
                    R.string.the_gutenberg_project
                )

                com.rick.ecatalogs.book.screen_book.bestseller_catalog.R.id.book_screen_book_bestseller_catalog_bestsellerfragment -> getString(
                    R.string.nyt_bestseller
                )

                com.rick.ecatalogs.book.screen_book.bestseller_favorites.R.id.book_screen_book_bestseller_favorites_bestsellerfavoritesfragment -> getString(
                    R.string.nyt_bestseller_favorite
                )

                com.rick.ecatalogs.book.screen_book.gutenberg_favorites.R.id.book_screen_book_gutenberg_favorites_gutenbergfavoritesfragment -> getString(
                    R.string.the_gutenberg_project_favorite
                )
                //Anime screen
                com.rick.ecatalogs.anime.screen_anime.anime_catalog.R.id.anime_screen_anime_anime_catalog_animecatalogfragment -> getString(
                    R.string.anime
                )

                com.rick.ecatalogs.anime.screen_anime.manga_catalog.R.id.anime_screen_anime_manga_catalog_mangacatalogfragment -> getString(
                    R.string.manga
                )

                com.rick.ecatalogs.anime.screen_anime.manga_favorites.R.id.anime_screen_anime_manga_favorites_mangafavoritefragment -> getString(
                    R.string.manga_favorite
                )

                com.rick.ecatalogs.anime.screen_anime.anime_favorites.R.id.anime_screen_anime_anime_favorites_animefavoritefragment -> getString(
                    R.string.anime_favorite
                )

                else -> getString(R.string.empty)
            }
        }

        viewModel.uiState.asLiveData().observe(this) { state ->
            when (state) {
                MainActivityUiState.Loading -> {}
                is MainActivityUiState.Success -> {
                    binding.navView.getHeaderView(0).findViewById<TextView>(R.id.name).text =
                        state.userData.userName
                    applyTheme(state.userData.themeConfig)
                    requestNotificationPermissionOnce(state.userData)
                }
            }
        }

        binding.navView.getHeaderView(0).setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(
                com.rick.ecatalogs.settings.screen_settings.R.id.settings_screen_settings_nav_graph
            )
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Notification taps while the app is running deliver a deep link here
        navController.handleDeepLink(intent)
    }

    private fun applyTheme(themeConfig: ThemeConfig) {
        val mode = when (themeConfig) {
            ThemeConfig.FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            ThemeConfig.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeConfig.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        }
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    /**
     * Asks for the notification permission (Android 13+) the first time the app runs with any
     * notification category enabled. Later changes go through the Settings screen.
     */
    private fun requestNotificationPermissionOnce(userData: SettingsUserData) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (userData.enabledNotifications.isEmpty()) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) return
        val prefs = getSharedPreferences(LAUNCH_PREFS, MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ASKED_NOTIFICATIONS, false)) return
        prefs.edit().putBoolean(KEY_ASKED_NOTIFICATIONS, true).apply()
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
//    private fun hideSystemBars() {
//        val windowInsetsController =
//            ViewCompat.getWindowInsetsController(window.decorView) ?: return
//        windowInsetsController.systemBarsBehavior =
//            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
//    }
}

private const val TAG = "mainActivity"
private const val LAUNCH_PREFS = "main_activity"
private const val KEY_ASKED_NOTIFICATIONS = "asked_notification_permission"
