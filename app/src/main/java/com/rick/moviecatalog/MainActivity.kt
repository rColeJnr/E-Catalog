package com.rick.moviecatalog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.rick.moviecatalog.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
                //Book screen≈
                || destination.id == com.rick.book.screen_book.bestseller_favorites.R.id.book_screen_book_bestseller_favorites_bestsellerfavoritesfragment
                || destination.id == com.rick.book.screen_book.gutenberg_search.R.id.book_screen_book_gutenberg_search_searchfragment
                || destination.id == com.rick.book.screen_book.gutenberg_favorites.R.id.book_screen_book_gutenberg_favorites_gutenbergfavoritesfragment

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

                //Book screen
                com.rick.book.screen_book.gutenberg_catalog.R.id.book_screen_book_gutenberg_catalog_gutenbergcatalogfragment -> getString(
                    R.string.the_gutenberg_project
                )

                com.rick.book.screen_book.bestseller_catalog.R.id.book_screen_book_bestseller_catalog_bestsellerfragment -> getString(
                    R.string.bestseller
                )

                //Anime screen
                com.rick.anime.screen_anime.anime_catalog.R.id.anime_screen_anime_anime_catalog_animecatalogfragment -> getString(
                    R.string.anime_screen_anime_anime
                )

                com.rick.anime.screen_anime.manga_catalog.R.id.anime_screen_anime_manga_catalog_mangacatalogfragment -> getString(
                    R.string.anime_screen_anime_manga
                )

                else -> getString(R.string.empty)
            }
        }

        // Add these three lines
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.menu_icon)
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
