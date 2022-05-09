package com.rick.moviecatalog.viewmodel

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rick.moviecatalog.R
import com.rick.moviecatalog.data.model.MovieCatalog
import com.rick.moviecatalog.data.model.Result
import com.rick.moviecatalog.data.util.GsonParser
import com.rick.moviecatalog.repository.MovieCatalogRepository
import com.rick.moviecatalog.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MovieCatalogViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _movieList = MutableLiveData<List<Result>>()
    val movieList: LiveData<List<Result>> = _movieList

    var movieMutableList = mutableListOf<Result>()

    private val _hasMore = MutableLiveData<Boolean>()
    val hasMore: LiveData<Boolean> = _hasMore

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private var paginationNumber = 15

    init {
        fetchMovieCatalog(paginationNumber)
    }

    private fun fetchMovieCatalog(pages: Int) {
        viewModelScope.launch {
            repository.getMovieCatalog(pages)
                .collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            _errorMessage.value = result.message ?: Resources.getSystem()
                                .getString(R.string.error_message)
                            _isRefreshing.value = false
                        }
                        is Resource.Loading -> {
                            _isLoading.value = result.isLoading
                        }
                        is Resource.Success -> {
                            _movieList.postValue(
                                result.data!!.results
                            )
                            _hasMore.value = result.data.hasMore
                            _isRefreshing.value = false
                        }
                    }
                }
        }
    }

    private fun jsonToJsonObject(result: Resource<MovieCatalog>): JSONObject {
        return GsonParser(Gson()).toJsonObject(result.data!!, object : TypeToken<MovieCatalog>() {}.type)
    }

    fun loadMoreData(){
        _isLoading.postValue(true)
        paginationNumber += 10
        fetchMovieCatalog(paginationNumber)
    }

    fun refreshData() {
        _isRefreshing.postValue(true)
        movieMutableList = mutableListOf()
        fetchMovieCatalog(15)
    }

}