package com.rick.movie.screen_movie.trending_movie_detatils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_movie.tmdb.trending_movie.TrendingMovie
import com.rick.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_movie_details.databinding.MovieScreenMovieTrendingMovieDetailsSimilarEntryBinding

//class DetailsImagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private val diffUtil = object : DiffUtil.ItemCallback<Item>() {
//        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
//            return oldItem.image == newItem.image
//        }
//
//        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    val imagesDiffer = AsyncListDiffer(this, diffUtil)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return ImagesViewHolder.create(parent)
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val image = imagesDiffer.currentList[position]
//        (holder as ImagesViewHolder).bind(image)
//    }
//
//    override fun getItemCount(): Int = imagesDiffer.currentList.size
//}

//class ImagesViewHolder(binding: ImageEntryBinding) :
//    RecyclerView.ViewHolder(binding.root) {
//    private val imageView = binding.movieImage
//
//    private lateinit var image: Item
//
//    fun bind(image: Item) {
//        this.image = image
//
//        val src = image.image
//        if (src.isNotBlank()) provideGlide(this.imageView, src)
//    }
//
//    companion object {
//        fun create(parent: ViewGroup): ImagesViewHolder {
//            val itemBinding = ImageEntryBinding
//                .inflate(LayoutInflater.from(parent.context), parent, false)
//            return ImagesViewHolder(itemBinding)
//        }
//    }
//}

class MovieSimilarDetailsAdapter(
    private val onSimilarClick: (TrendingMovie) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil =
        object : DiffUtil.ItemCallback<TrendingMovie>() {
            override fun areItemsTheSame(
                oldItem: TrendingMovie,
                newItem: TrendingMovie
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TrendingMovie,
                newItem: TrendingMovie
            ): Boolean {
                return oldItem == newItem
            }
        }

    val similarsDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SimilarsViewHolder.create(parent, onSimilarClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val similar = similarsDiffer.currentList[position]
        (holder as SimilarsViewHolder).bind(similar)
    }

    override fun getItemCount(): Int = similarsDiffer.currentList.size
}

class SimilarsViewHolder(
    binding: MovieScreenMovieTrendingMovieDetailsSimilarEntryBinding,
    onSimilarClick: (TrendingMovie) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    private val image = binding.similarImage
    private val title = binding.similarTitle
    private val card = binding.root

    private lateinit var similar: TrendingMovie

    init {
        card.setOnClickListener {
            onSimilarClick(similar)
        }
    }

    fun bind(similar: TrendingMovie) {
        this.similar = similar
        this.title.text = similar.title

        val src = similar.image
        if (src.isNotBlank()) provideGlide(this.image, getTmdbImageUrl(src))
    }

    companion object {
        fun create(parent: ViewGroup, onSimilarClick: (TrendingMovie) -> Unit): SimilarsViewHolder {
            val itemBinding = MovieScreenMovieTrendingMovieDetailsSimilarEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return SimilarsViewHolder(itemBinding, onSimilarClick)
        }
    }
}