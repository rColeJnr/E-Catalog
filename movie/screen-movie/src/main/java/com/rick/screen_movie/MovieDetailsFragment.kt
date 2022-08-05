package com.rick.screen_movie

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.rick.data_movie.Movie
import com.rick.screen_movie.databinding.FragmentMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment: Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieCatalogViewModel by activityViewModels()
    private lateinit var movie: Movie

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val safeArgs = MovieDetailsFragmentArgs.fromBundle(it)
            movie = safeArgs.movie
        }

        binding.movieName.text = movie.title
        Glide.with(requireContext()).load(movie.multimedia.src).into(binding.movieImage)
        binding.movieArticleLink.text = getString(R.string.read_article, movie.link.url)
        binding.moviePublicationDate.text = getString(R.string.movie_published_on, movie.openingDate)
        binding.movieSummary.text = movie.summary
        binding.movieRating.text = movie.rating

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}