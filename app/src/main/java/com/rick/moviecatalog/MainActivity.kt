package com.rick.moviecatalog

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.rick.moviecatalog.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                //Movie screen1
                com.rick.screen_movie.R.id.movieCatalogFragment,
                com.rick.screen_movie.R.id.trendingMovieFragment,
                com.rick.screen_movie.R.id.tvSeriesFragment,
                com.rick.screen_movie.R.id.movieFavoriteFragment,
                //Book screen
                com.rick.screen_book.R.id.bookCatalogFragment,
                com.rick.screen_book.R.id.bestsellerFragment,
                com.rick.screen_book.R.id.bookFavoritesFragment,
                //Anime screen
                com.rick.screen_anime.R.id.animeCatalogFragment,
                com.rick.screen_anime.R.id.mangaCatalogFragment,
                com.rick.screen_anime.R.id.jikanFavoriteFragment,
                //Auth screen
                com.rick.moviecatalog.R.id.authenticationFragment
            ),
            binding.drawerLayout
        )
        toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        )
        toolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.menu_icon, null)

        binding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Activity toolbar or Fragment toolbar
            toolbar.visibility = if (
                //Movie screen
                destination.id == com.rick.screen_movie.R.id.seriesSearchFragment
                || destination.id == com.rick.screen_movie.R.id.movieDetailsFragment
                || destination.id == com.rick.screen_movie.R.id.tvDetailsFragment
                || destination.id == com.rick.screen_movie.R.id.searchFragment
                || destination.id == com.rick.screen_movie.R.id.moving_pictures_favorites_graph

                //Book screen
                || destination.id == com.rick.screen_book.R.id.bookDetailsFragment
                || destination.id == com.rick.screen_book.R.id.bookSearchFragment
                || destination.id == com.rick.screen_book.R.id.book_favorite_graph

                //Anime screen
                || destination.id == com.rick.screen_anime.R.id.animeSearchJikanFragment
                || destination.id == com.rick.screen_anime.R.id.animeDetailsJikanFragment
                || destination.id == com.rick.screen_anime.R.id.mangaDetailsJikanFragment
                || destination.id == com.rick.screen_anime.R.id.mangaSearchJikanFragment
                || destination.id == com.rick.screen_anime.R.id.animation_favorite_graph

                //Auth screen
                || destination.id == com.rick.moviecatalog.R.id.authenticationFragment
            )
                View.GONE
            else
                View.VISIBLE

            //Toolbar title
            binding.toolbarText.text = when (destination.id) {
                //Movie screen
                com.rick.screen_movie.R.id.movieCatalogFragment -> getString(R.string.nyt_articles)
                com.rick.screen_movie.R.id.trendingMovieFragment -> getString(R.string.trending_movies)
                com.rick.screen_movie.R.id.tvSeriesFragment -> getString(R.string.trending_series)
                com.rick.screen_movie.R.id.movieFavoriteFragment -> getString(com.rick.screen_movie.R.string.movie_fav)

                //Book screen
                com.rick.screen_book.R.id.bookCatalogFragment -> getString(R.string.the_gutenberg_project)
                com.rick.screen_book.R.id.bestsellerFragment -> getString(R.string.bestseller)
                com.rick.screen_book.R.id.bookFavoritesFragment -> getString(com.rick.screen_book.R.string.fav_book)

                //Anime screen
                com.rick.screen_anime.R.id.animeCatalogFragment -> getString(R.string.anime)
                com.rick.screen_anime.R.id.mangaCatalogFragment -> getString(R.string.manga)
                com.rick.screen_anime.R.id.jikanFavoriteFragment -> getString(com.rick.screen_anime.R.string.jikan_fav)
                else -> getString(com.rick.moviecatalog.R.string.empty)
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
