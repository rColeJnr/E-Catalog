package com.rick.moviecatalog

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
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
        toolbar.title = getString(R.string.empty)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                com.rick.screen_movie.R.id.movieCatalogFragment,
                com.rick.screen_book.R.id.bookCatalogFragment,
                com.rick.screen_movie.R.id.tvSeriesFragment,
                com.rick.screen_anime.R.id.animeCatalogFragment,
                com.rick.screen_anime.R.id.mangaCatalogFragment
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
            toolbar.visibility = if (
                destination.id == com.rick.screen_movie.R.id.seriesSearchFragment
                || destination.id == com.rick.screen_movie.R.id.movieDetailsFragment2
                || destination.id == com.rick.screen_movie.R.id.tvDetailsFragment
                || destination.id == com.rick.screen_movie.R.id.searchFragment
                || destination.id == com.rick.screen_book.R.id.bookDetailsFragment
                || destination.id == com.rick.screen_book.R.id.bookSearchFragment
                || destination.id == com.rick.screen_anime.R.id.animeSearchJikanFragment
                || destination.id == com.rick.screen_anime.R.id.animeDetailsJikanFragment
                || destination.id == com.rick.screen_anime.R.id.mangaDetailsJikanFragment
                || destination.id == com.rick.screen_anime.R.id.mangaSearchJikanFragment
            )
                View.GONE
            else
                View.VISIBLE

            binding.toolbarText.text = when (destination.id) {
                com.rick.screen_book.R.id.bookCatalogFragment -> getString(R.string.books)
                com.rick.screen_book.R.id.bookFavoritesFragment -> getString(R.string.favorites)
                com.rick.screen_movie.R.id.movieCatalogFragment -> getString(R.string.movie)
                com.rick.screen_movie.R.id.tvSeriesFragment -> getString(R.string.series)
                com.rick.screen_movie.R.id.movieFavoriteFragment -> getString(R.string.favorites)
                com.rick.screen_movie.R.id.seriesFavoriteFragment -> getString(R.string.favorites)
                com.rick.screen_anime.R.id.animeCatalogFragment -> getString(R.string.anime)
                com.rick.screen_anime.R.id.mangaCatalogFragment -> getString(R.string.manga)
                com.rick.screen_anime.R.id.animeFavoriteFragment -> getString(R.string.favorites)
                com.rick.screen_anime.R.id.mangaFavoriteFragment -> getString(R.string.favorites)
                else -> getString(R.string.empty)
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
