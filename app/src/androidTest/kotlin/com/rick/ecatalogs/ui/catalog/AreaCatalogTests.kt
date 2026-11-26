package com.rick.ecatalogs.ui.catalog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.rick.ecatalogs.R
import com.rick.ecatalogs.ui.assertDestination
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import com.rick.ecatalogs.anime.screen_anime.anime_catalog.R as AnimeCatalogR
import com.rick.ecatalogs.anime.screen_anime.anime_details.R as AnimeDetailsR
import com.rick.ecatalogs.anime.screen_anime.anime_favorites.R as AnimeFavoritesR
import com.rick.ecatalogs.anime.screen_anime.anime_search.R as AnimeSearchR
import com.rick.ecatalogs.anime.screen_anime.manga_catalog.R as MangaCatalogR
import com.rick.ecatalogs.anime.screen_anime.manga_details.R as MangaDetailsR
import com.rick.ecatalogs.anime.screen_anime.manga_favorites.R as MangaFavoritesR
import com.rick.ecatalogs.anime.screen_anime.manga_search.R as MangaSearchR
import com.rick.ecatalogs.book.screen_book.bestseller_catalog.R as BestsellerCatalogR
import com.rick.ecatalogs.book.screen_book.bestseller_favorites.R as BestsellerFavoritesR
import com.rick.ecatalogs.book.screen_book.common.R as BookCommonR
import com.rick.ecatalogs.book.screen_book.gutenberg_catalog.R as GutenbergCatalogR
import com.rick.ecatalogs.book.screen_book.gutenberg_favorites.R as GutenbergFavoritesR
import com.rick.ecatalogs.book.screen_book.gutenberg_search.R as GutenbergSearchR
import com.rick.ecatalogs.movie.screen_movie.article_catalog.R as ArticleCatalogR
import com.rick.ecatalogs.movie.screen_movie.article_favorite.R as ArticleFavoriteR
import com.rick.ecatalogs.movie.screen_movie.article_search.R as ArticleSearchR
import com.rick.ecatalogs.movie.screen_movie.common.R as MovieCommonR
import com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog.R as MovieCatalogR
import com.rick.ecatalogs.movie.screen_movie.trending_movie_details.R as MovieDetailsR
import com.rick.ecatalogs.movie.screen_movie.trending_movie_favorite.R as MovieFavoriteR
import com.rick.ecatalogs.movie.screen_movie.trending_movie_search.R as MovieSearchR
import com.rick.ecatalogs.movie.screen_movie.trending_series_catalog.R as SeriesCatalogR
import com.rick.ecatalogs.movie.screen_movie.trending_series_details.R as SeriesDetailsR
import com.rick.ecatalogs.movie.screen_movie.trending_series_favorite.R as SeriesFavoriteR
import com.rick.ecatalogs.movie.screen_movie.trending_series_search.R as SeriesSearchR

/** Trending series (home). Favorited from the catalog card's heart; details have no favorite. */
@HiltAndroidTest
class TrendingSeriesCatalogTest : CatalogFlowTest() {
    override val drawerTitle = R.string.trending_series
    override val catalogDestination =
        SeriesCatalogR.id.movie_screen_movie_trending_series_catalog_trendingseriesfragment
    override val catalogList = ListSpec(
        SeriesCatalogR.id.recyclerView,
        SeriesCatalogR.id.movie_entry_card_view,
        SeriesCatalogR.id.movie_name,
        SeriesCatalogR.id.favorite,
    )
    override val detailsDestination =
        SeriesDetailsR.id.movie_screen_movie_trending_series_details_trendingseriesdetailsfragment
    override val searchItem = SeriesCatalogR.id.search_imdb
    override val searchDestination =
        SeriesSearchR.id.movie_screen_movie_trending_series_search_trendingseriessearchfragment
    override val searchQuery = "Breaking" // search_series.json: Breaking Bad
    override val searchResults = ListSpec(
        SeriesSearchR.id.list,
        SeriesSearchR.id.search_entry_card_view,
        SeriesSearchR.id.movie_name,
    )
    override val favoritesItem = SeriesCatalogR.id.fav_imdb
    override val favoritesDestination =
        SeriesFavoriteR.id.movie_screen_movie_trending_series_favorite_trendingseriesfavoritefragment
}

/** Trending movies. Favorited from the catalog card's heart; details have no favorite. */
@HiltAndroidTest
class TrendingMoviesCatalogTest : CatalogFlowTest() {
    override val drawerTitle = R.string.trending_movies
    override val catalogDestination =
        MovieCatalogR.id.movie_screen_movie_trending_movie_catalog_trendingmoviefragment
    override val catalogList = ListSpec(
        MovieCatalogR.id.recyclerView,
        MovieCatalogR.id.movie_entry_card_view,
        MovieCatalogR.id.movie_name,
        MovieCatalogR.id.favorite,
    )
    override val detailsDestination =
        MovieDetailsR.id.movie_screen_movie_trending_movie_details_moviedetailsfragment
    override val searchItem = MovieCatalogR.id.search_imdb
    override val searchDestination =
        MovieSearchR.id.movie_screen_movie_trending_movie_search_trendingmoviesearchfragment
    override val searchQuery = "Fight Club" // searchMovie.json
    override val searchResults = ListSpec(
        MovieSearchR.id.list,
        MovieSearchR.id.movie_entry_card_view,
        MovieSearchR.id.movie_name,
    )
    override val favoritesItem = MovieCatalogR.id.fav_imdb
    override val favoritesDestination =
        MovieFavoriteR.id.movie_screen_movie_trending_movie_favorite_trenidngmoviefavoritefragment
}

/**
 * NYT movie reviews. Tapping a review opens a dialog (not a destination); it's favorited from
 * the dialog's heart.
 */
@HiltAndroidTest
class MovieReviewsCatalogTest : CatalogFlowTest() {
    override val drawerTitle = R.string.movie_reviews
    override val catalogDestination = ArticleCatalogR.id.movie_screen_movie_article_catalog_articlefragment
    override val catalogList = ListSpec(
        ArticleCatalogR.id.recycler_view,
        ArticleCatalogR.id.movie_entry_card_view,
        ArticleCatalogR.id.movie_name,
        ArticleCatalogR.id.favorite,
    )
    override val detailsDestination = catalogDestination // the dialog isn't a destination
    override val searchItem = ArticleCatalogR.id.search_imdb
    override val searchDestination = ArticleSearchR.id.movie_screen_movie_article_search_articlesearchfragment
    override val searchQuery = "Gentleman" // search_movie_article.json
    override val searchResults = ListSpec(
        ArticleSearchR.id.list,
        ArticleSearchR.id.movie_entry_card_view,
        ArticleSearchR.id.movieName,
    )
    override val favoritesItem = ArticleCatalogR.id.fav_imdb
    override val favoritesDestination =
        ArticleFavoriteR.id.movie_screen_movie_article_favorite_articlefavoritefragment

    override fun openItemAndAssertDetails(title: String) {
        openCard(catalogList, title)
        onView(allOf(withId(MovieCommonR.id.title), withText(title)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        app.assertDestination(catalogDestination)
    }

    override fun favorite(title: String) {
        openItemAndAssertDetails(title)
        onView(withId(MovieCommonR.id.favorite)).inRoot(isDialog()).perform(click())
        onView(withId(MovieCommonR.id.close_dialog)).inRoot(isDialog()).perform(click())
    }
}

/** Anime. Favorited from the catalog card's heart (details only show MAL's favorite count). */
@HiltAndroidTest
class AnimeCatalogTest : CatalogFlowTest() {
    override val drawerTitle = R.string.anime
    override val catalogDestination = AnimeCatalogR.id.anime_screen_anime_anime_catalog_animecatalogfragment
    override val catalogList = ListSpec(
        AnimeCatalogR.id.jikan_recycler_view,
        AnimeCatalogR.id.movie_entry_card_view,
        AnimeCatalogR.id.title,
        AnimeCatalogR.id.favorite,
    )
    override val detailsDestination = AnimeDetailsR.id.anime_screen_anime_anime_details_animedetailsfragment
    override val searchItem = AnimeCatalogR.id.search
    override val searchDestination = AnimeSearchR.id.anime_screen_anime_anime_search_animesearchfragment
    override val searchQuery = "Naruto" // searchanime.json
    override val searchResults = ListSpec(
        AnimeSearchR.id.list,
        AnimeSearchR.id.anime_entry_card_view,
        AnimeSearchR.id.title,
    )
    override val favoritesItem = AnimeCatalogR.id.favorite
    override val favoritesDestination =
        AnimeFavoritesR.id.anime_screen_anime_anime_favorites_animefavoritefragment
}

/** Manga. Favorited from the catalog card's heart. */
@HiltAndroidTest
class MangaCatalogTest : CatalogFlowTest() {
    override val drawerTitle = R.string.manga
    override val catalogDestination = MangaCatalogR.id.anime_screen_anime_manga_catalog_mangacatalogfragment
    override val catalogList = ListSpec(
        MangaCatalogR.id.jikan_recycler_view,
        MangaCatalogR.id.movie_entry_card_view,
        MangaCatalogR.id.title,
        MangaCatalogR.id.favorite,
    )
    override val detailsDestination = MangaDetailsR.id.anime_screen_anime_manga_details_mangadetailsfragment
    override val searchItem = MangaCatalogR.id.search
    override val searchDestination = MangaSearchR.id.anime_screen_anime_manga_search_mangasearchfragment
    override val searchQuery = "Naruto" // searchmanga.json
    override val searchResults = ListSpec(
        MangaSearchR.id.list,
        MangaSearchR.id.movie_entry_card_view,
        MangaSearchR.id.title,
    )
    override val favoritesItem = MangaCatalogR.id.favorite
    override val favoritesDestination =
        MangaFavoritesR.id.anime_screen_anime_manga_favorites_mangafavoritefragment
}

/** NYT bestsellers. No search screen yet; favorited from the details toolbar. */
@HiltAndroidTest
class BestsellersCatalogTest : CatalogFlowTest() {
    override val drawerTitle = R.string.nyt_bestseller
    override val catalogDestination = BestsellerCatalogR.id.book_screen_book_bestseller_catalog_bestsellerfragment
    override val catalogList = ListSpec(
        BestsellerCatalogR.id.recyclerView,
        BestsellerCatalogR.id.book_entry_card_view,
        BestsellerCatalogR.id.title,
        BestsellerCatalogR.id.favorite,
    )
    override val detailsDestination = BestsellerCatalogR.id.book_screen_book_bestseller_catalog_detailsfragment
    override val searchItem: Int? = null // bestseller_search is commented out
    override val favoritesItem = BestsellerCatalogR.id.favorite
    override val favoritesDestination =
        BestsellerFavoritesR.id.book_screen_book_bestseller_favorites_bestsellerfavoritesfragment

    override fun favorite(title: String) {
        openItemAndAssertDetails(title)
        app.clickToolbarItem(
            BestsellerCatalogR.id.details_favorite,
            toolbarId = BestsellerCatalogR.id.details_toolbar,
        )
        androidx.test.espresso.Espresso.pressBack()
        app.assertDestination(catalogDestination)
    }
}

/** Project Gutenberg. Tapping a book opens the reader (its text needs the network, not checked). */
@HiltAndroidTest
class GutenbergCatalogTest : CatalogFlowTest() {
    override val drawerTitle = R.string.the_gutenberg_project
    override val catalogDestination =
        GutenbergCatalogR.id.book_screen_book_gutenberg_catalog_gutenbergcatalogfragment
    override val catalogList = ListSpec(
        GutenbergCatalogR.id.recyclerView,
        GutenbergCatalogR.id.book_entry_card_view,
        GutenbergCatalogR.id.title,
        GutenbergCatalogR.id.favorite,
    )
    override val detailsDestination = BookCommonR.id.book_screen_book_common_readerfragment
    override val searchItem = GutenbergCatalogR.id.search
    override val searchDestination = GutenbergSearchR.id.book_screen_book_gutenberg_search_searchfragment
    override val searchQuery = "Great" // searchgutenberg.json: Great Expectations
    override val searchResults = ListSpec(
        GutenbergSearchR.id.list,
        GutenbergSearchR.id.book_entry_card_view,
        GutenbergSearchR.id.title,
    )
    override val favoritesItem = GutenbergCatalogR.id.favorite
    override val favoritesDestination =
        GutenbergFavoritesR.id.book_screen_book_gutenberg_favorites_gutenbergfavoritesfragment
}
