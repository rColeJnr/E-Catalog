package com.rick.book.screen_book.bestseller_catalog

import android.R.attr.author
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rick.book.screen_book.bestseller_catalog.databinding.BookScreenBookBestsellerCatalogCarouselBookEntryBinding
import com.rick.data.model_book.bestseller.UserBestseller
import com.rick.data.ui_components.common.provideGlide

class BestsellerCarouselAdapter(
    private val onItemClick: (UserBestseller) -> Unit,
    private val onFavoriteClick: (View, String, Boolean) -> Unit
) : ListAdapter<UserBestseller, BestsellerCarouselAdapter.CarouselViewHolder>(CarouselDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = BookScreenBookBestsellerCatalogCarouselBookEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarouselViewHolder(
            binding = binding,
            onBookClick = onItemClick,
            onFavoriteClick = onFavoriteClick
        )
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class CarouselViewHolder(
        binding: BookScreenBookBestsellerCatalogCarouselBookEntryBinding,
        private val onBookClick: (UserBestseller) -> Unit,
        private val onFavoriteClick: (View, String, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val image = binding.image
        private val rank = binding.rank
        private val favorite = binding.favorite
        private val title = binding.title
        private val author = binding.author
        private val weeksTrending = binding.weeksTrending
        private val resources = itemView.resources

        private lateinit var book: UserBestseller

        init {
            favorite.setOnClickListener { view ->
                onFavoriteClick(view, book.id, book.isFavorite)
            }

            binding.root.setOnClickListener {
                onBookClick(book)
            }
        }

        fun bind(book: UserBestseller) {
            this.book = book
            if (book.image.isNotEmpty()) {
                provideGlide(image, book.image)
            }
            title.text = book.title.lowercase().replaceFirstChar { it.titlecase() }
            author.text = book.author
            rank.text =
                resources.getString(R.string.book_screen_book_bestseller_catalog_rank, book.rank)
            weeksTrending.text = resources.getString(
                R.string.book_screen_book_bestseller_catalog_weeksOnList,
                book.weeksOnList
            )

            favorite.setImageResource(
                if (book.isFavorite) {
                    com.rick.data.ui_design.R.drawable.data_ui_design_favorite_filled
                } else {
                    com.rick.data.ui_design.R.drawable.data_ui_design_favorite_outlined
                }
            )
        }
    }

    object CarouselDiffCallback : DiffUtil.ItemCallback<UserBestseller>() {
        override fun areItemsTheSame(oldItem: UserBestseller, newItem: UserBestseller): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UserBestseller, newItem: UserBestseller): Boolean =
            oldItem == newItem
    }
}