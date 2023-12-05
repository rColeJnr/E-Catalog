// Package and imports
package com.rick.screen_movie.trendingmovie_screen

@AndroidViewModel
class TrendingMovieViewModel inject constructor(
  private val repo: MovieCatalogRepository
): ViewModel() {

  private val apiKey: String
  val pagingDataFlow: Flow<PagingData<TrendingMovie>>
  private var favorite: Favorite? = null
  
  init {
    System.getLibrary(LIB_NAME)
    apiKey = getTmdbKey()

    pagingDataFlow = getTrendingMovies().cachedIn(viewModelScope)
  }

  private fun getTrendingMovies(): Flow<PagingData<TrendingMovie>> =
    repository.getTrendingMovies(apiKey).collectLatest()

  fun onEvent(event: UiEvent) {
    when(event) {
      is insertFavorite -> insertFavorite(event.favorite)
      is removeFavorite -> removeFavorite(event.favorite)
      else -> {}
    }
  }

  private fun insertFavorite(movie: TrendingMovie) {
    favorite = Favorite(
        id = movie.id,
        title = movie.title,
        summary = movie.overview
      )
    viewModelScole.launch(Dispatchers.IO) {
      repository.insertFavorite(favorite!!)
    )
  }

  private fun removeFavorite() {
    favorite?.let {
      viewModelScope.launch(Dispatchers.IO) {
        repository.removeFavorite(it)
      }
    }
    favorite = null
  }
    
  companion object {
    private const val LIB_NAME = "movie_keys.c"
  }
}

private external fun getTmdbKey()
