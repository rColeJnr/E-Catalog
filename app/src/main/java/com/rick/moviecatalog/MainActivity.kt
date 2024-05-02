package com.rick.moviecatalog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.textview.MaterialTextView
import com.rick.moviecatalog.databinding.ActivityMainBinding
import com.rick.moviecatalog.databinding.NavHeaderBinding
import com.rick.settings.screen_settings.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderBinding: NavHeaderBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: MainActivityViewModel by viewModels()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navHeaderBinding = NavHeaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                //Movie screen1
                com.rick.movie.screen_movie.article_catalog.R.id.movie_screen_movie_article_catalog_articlefragment,
                com.rick.movie.screen_movie.trending_movie_catalog.R.id.movie_screen_movie_trending_movie_catalog_trendingmoviefragment,
                com.rick.movie.screen_movie.trending_series_catalog.R.id.movie_screen_movie_trending_series_catalog_trendingseriesfragment,
                //Book screen
                com.rick.book.screen_book.gutenberg_catalog.R.id.book_screen_book_gutenberg_catalog_gutenbergcatalogfragment,
                com.rick.book.screen_book.bestseller_catalog.R.id.book_screen_book_bestseller_catalog_bestsellerfragment,
                //Anime screen
                com.rick.anime.screen_anime.anime_catalog.R.id.anime_screen_anime_anime_catalog_animecatalogfragment,
                com.rick.anime.screen_anime.manga_catalog.R.id.anime_screen_anime_manga_catalog_mangacatalogfragment,
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
                destination.id == com.rick.movie.screen_movie.trending_series_search.R.id.movie_screen_movie_trending_series_search_trendingseriessearchfragment
                || destination.id == com.rick.movie.screen_movie.trending_movie_search.R.id.movie_screen_movie_trending_movie_search_trendingmoviesearchfragment
                || destination.id == com.rick.movie.screen_movie.article_search.R.id.movie_screen_movie_article_search_articlesearchfragment
                || destination.id == com.rick.movie.screen_movie.trending_movie_details.R.id.movie_screen_movie_trending_movie_details_moviedetailsfragment
                || destination.id == com.rick.movie.screen_movie.trending_series_details.R.id.movie_screen_movie_trending_series_details_trendingseriesdetailsfragment
                //Book screen
                || destination.id == com.rick.book.screen_book.gutenberg_search.R.id.book_screen_book_gutenberg_search_searchfragment

                //Anime screen
                || destination.id == com.rick.anime.screen_anime.anime_search.R.id.anime_screen_anime_anime_search_animesearchfragment
                || destination.id == com.rick.anime.screen_anime.manga_search.R.id.anime_screen_anime_manga_search_mangasearchfragment
                || destination.id == com.rick.anime.screen_anime.manga_details.R.id.anime_screen_anime_manga_details_mangadetailsfragment
                || destination.id == com.rick.anime.screen_anime.anime_details.R.id.anime_screen_anime_anime_details_animedetailsfragment

                //Auth screen
                || destination.id == com.rick.auth_screen.R.id.authenticationFragment
            )
                View.GONE
            else
                View.VISIBLE

            //Toolbar title
            binding.toolbarText.text = when (destination.id) {
                //Movie screen
                com.rick.movie.screen_movie.article_catalog.R.id.movie_screen_movie_article_catalog_articlefragment -> getString(
                    R.string.nyt_articles
                )

                com.rick.movie.screen_movie.trending_movie_catalog.R.id.movie_screen_movie_trending_movie_catalog_trendingmoviefragment -> getString(
                    R.string.trending_movies
                )

                com.rick.movie.screen_movie.trending_series_catalog.R.id.movie_screen_movie_trending_series_catalog_trendingseriesfragment -> getString(
                    R.string.trending_series
                )

                com.rick.movie.screen_movie.trending_series_favorite.R.id.movie_screen_movie_trending_series_favorite_trendingseriesfavoritefragment -> getString(
                    R.string.trending_series_favorite
                )

                com.rick.movie.screen_movie.trending_movie_favorite.R.id.movie_screen_movie_trending_movie_favorite_trenidngmoviefavoritefragment -> getString(
                    R.string.trending_movies_favorite
                )

                com.rick.movie.screen_movie.article_favorite.R.id.movie_screen_movie_article_favorite_articlefavoritefragment -> getString(
                    R.string.nyt_articles_favorite
                )

                //Book screen
                com.rick.book.screen_book.gutenberg_catalog.R.id.book_screen_book_gutenberg_catalog_gutenbergcatalogfragment -> getString(
                    R.string.the_gutenberg_project
                )

                com.rick.book.screen_book.bestseller_catalog.R.id.book_screen_book_bestseller_catalog_bestsellerfragment -> getString(
                    R.string.nyt_bestseller
                )

                com.rick.book.screen_book.bestseller_favorites.R.id.book_screen_book_bestseller_favorites_bestsellerfavoritesfragment -> getString(
                    R.string.nyt_bestseller_favorite
                )

                com.rick.book.screen_book.gutenberg_favorites.R.id.book_screen_book_gutenberg_favorites_gutenbergfavoritesfragment -> getString(
                    R.string.the_gutenberg_project_favorite
                )
                //Anime screen
                com.rick.anime.screen_anime.anime_catalog.R.id.anime_screen_anime_anime_catalog_animecatalogfragment -> getString(
                    R.string.anime
                )

                com.rick.anime.screen_anime.manga_catalog.R.id.anime_screen_anime_manga_catalog_mangacatalogfragment -> getString(
                    R.string.manga
                )

                com.rick.anime.screen_anime.manga_favorites.R.id.anime_screen_anime_manga_favorites_mangafavoritefragment -> getString(
                    R.string.manga_favorite
                )

                com.rick.anime.screen_anime.anime_favorites.R.id.anime_screen_anime_anime_favorites_animefavoritefragment -> getString(
                    R.string.anime_favorite
                )

                else -> getString(R.string.empty)
            }
        }

        val settingsState = viewModel.uiState.asLiveData()

        settingsState.observe(this) { state ->
            when (state) {
                MainActivityUiState.Loading -> {}
                is MainActivityUiState.Success -> {
                    findViewById<MaterialTextView>(R.id.name).text = state.userData.userName
                }
            }
        }


        binding.navView.getHeaderView(0).setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            viewModel.showSettingsDialog.value = true
        }

        viewModel.showSettingsDialog.observe(this) { show ->
            if (show) {
                binding.dialogView.setContent {
                    SettingsDialog(
                        onDismiss = { viewModel.showSettingsDialog.value = false },
                        username = findViewById<MaterialTextView>(R.id.name).text.toString()
                    )
                }
            } else {
                binding.dialogView.setContent { }
            }
        }

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
