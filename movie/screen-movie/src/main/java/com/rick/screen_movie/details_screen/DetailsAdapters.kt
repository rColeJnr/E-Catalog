package com.rick.screen_movie.details_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_movie.imdb_am_not_paying.movie_model.Actor
import com.rick.data_movie.tmdb.movie.Similar
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.ActorsEntryBinding
import com.rick.screen_movie.databinding.SimilarEntryBinding
import com.rick.screen_movie.util.provideGlide

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

class SimilarDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Similar>() {
        override fun areItemsTheSame(oldItem: Similar, newItem: Similar): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Similar, newItem: Similar): Boolean {
            return oldItem == newItem
        }
    }

    val similarsDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SimilarsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val similar = similarsDiffer.currentList[position]
        (holder as SimilarsViewHolder).bind(similar)
    }

    override fun getItemCount(): Int = similarsDiffer.currentList.size
}

class SimilarsViewHolder(binding: SimilarEntryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val image = binding.similarImage
    private val title = binding.similarTitle

    private lateinit var similar: Similar

    fun bind(similar: Similar) {
        this.similar = similar
        this.title.text = similar.title

        val src = similar.posterPath
        if (src.isNotBlank()) provideGlide(this.image, src)
    }

    companion object {
        fun create(parent: ViewGroup): SimilarsViewHolder {
            val itemBinding = SimilarEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return SimilarsViewHolder(itemBinding)
        }
    }
}

class ActorDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Actor>() {
        override fun areItemsTheSame(oldItem: Actor, newItem: Actor): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Actor, newItem: Actor): Boolean {
            return oldItem == newItem
        }
    }

    val actorsDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ActorsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val actor = actorsDiffer.currentList[position]
        (holder as ActorsViewHolder).bind(actor)
    }

    override fun getItemCount(): Int = actorsDiffer.currentList.size
}

class ActorsViewHolder(binding: ActorsEntryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val image = binding.actorImage
    private val name = binding.actorName
    private val character = binding.actorCharacter

    private lateinit var actor: Actor

    fun bind(actor: Actor) {
        this.actor = actor
        this.name.text = actor.name
        this.character.text =
            this.itemView.context.getString(R.string.as_character, actor.asCharacter)

        val src = actor.image
        if (src.isNotBlank()) provideGlide(this.image, src)
    }

    companion object {
        fun create(parent: ViewGroup): ActorsViewHolder {
            val itemBinding = ActorsEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return ActorsViewHolder(itemBinding)
        }
    }
}