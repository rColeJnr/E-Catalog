package com.rick.screen_movie.details_screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.rick.data_movie.imdb.movie_model.Actor
import com.rick.data_movie.imdb.movie_model.IMDBMovie
import com.rick.data_movie.imdb.movie_model.Image
import com.rick.data_movie.imdb.movie_model.Similar
import com.rick.screen_movie.databinding.ActorsEntryBinding
import com.rick.screen_movie.databinding.FragmentMovieDetailsBinding
import com.rick.screen_movie.databinding.ImageEntryBinding
import com.rick.screen_movie.databinding.SimilarEntryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var movie: IMDBMovie

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

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

// TODO MERGE ALL ADAPTERS
class DetailsImagesAdapter(
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }

    val imagesDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImagesViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = imagesDiffer.currentList[position]
        (holder as ImagesViewHolder).bind(glide, image)
    }

    override fun getItemCount(): Int = imagesDiffer.currentList.size
}

class ImagesViewHolder(binding: ImageEntryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val imageView = binding.movieImage
    private val imageTitle = binding.imageTitle

    private lateinit var image: Image

    fun bind(glide: RequestManager, image: Image) {
        this.image = image
        this.imageTitle.text = image.title
        if (image.image.isNotEmpty()) {
            glide
                .load(image.image)
                .into(this.imageView)
        }
    }

    companion object {
        fun create(parent: ViewGroup): ImagesViewHolder {
            val itemBinding = ImageEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return ImagesViewHolder(itemBinding)
        }
    }
}


class ActorDetailsAdapter(
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Actor>() {
        override fun areItemsTheSame(oldItem: Actor, newItem: Actor): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Actor, newItem: Actor): Boolean {
            return oldItem == newItem
        }
    }

    val imagesDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImagesViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val actor = imagesDiffer.currentList[position]
        (holder as ActorsViewHolder).bind(glide, actor)
    }

    override fun getItemCount(): Int = imagesDiffer.currentList.size
}

class ActorsViewHolder(binding: ActorsEntryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val image = binding.actorImage
    private val name = binding.actorName
    private val character = binding.actorCharacter

    private lateinit var actor: Actor

    fun bind(glide: RequestManager, actor: Actor) {
        this.actor = actor
        this.name.text = actor.name
        this.character.text = actor.asCharacter
        if (actor.image.isNotEmpty()) {
            glide
                .load(actor.image)
                .into(this.image)
        }
    }

    companion object {
        fun create(parent: ViewGroup): ActorsViewHolder {
            val itemBinding = ActorsEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return ActorsViewHolder(itemBinding)
        }
    }
}


class SimilarDetailsAdapter(
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Similar>() {
        override fun areItemsTheSame(oldItem: Similar, newItem: Similar): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Similar, newItem: Similar): Boolean {
            return oldItem == newItem
        }
    }

    val imagesDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SimilarsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val similar = imagesDiffer.currentList[position]
        (holder as SimilarsViewHolder).bind(glide, similar)
    }

    override fun getItemCount(): Int = imagesDiffer.currentList.size
}

class SimilarsViewHolder(binding: SimilarEntryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val image = binding.similarImage
    private val title = binding.similarTitle

    private lateinit var similar: Similar

    fun bind(glide: RequestManager, similar: Similar) {
        this.similar = similar
        this.title.text = similar.title
        if (similar.image.isNotEmpty()) {
            glide
                .load(similar.image)
                .into(this.image)
        }
    }

    companion object {
        fun create(parent: ViewGroup): SimilarsViewHolder {
            val itemBinding = SimilarEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return SimilarsViewHolder(itemBinding)
        }
    }
}