package com.rick.book.screen_book.bestseller_catalog

import android.R.attr.name
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rick.book.screen_book.bestseller_catalog.databinding.BookScreenBookBestsellerCatalogCategoryEntryBinding
import java.util.Locale

class BestsellerCategoryAdapter(
    private val onItemClick: (Int) -> Unit,
//    private val selectedPosition: Int
) : ListAdapter<BookGenre, BestsellerCategoryAdapter.CategoryViewHolder>(CategoryDiffCallback) {

    private var selectedPosition = 0

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CategoryViewHolder {
        val binding = BookScreenBookBestsellerCatalogCategoryEntryBinding.inflate(
            LayoutInflater.from(p0.context), p0, false
        )
        return CategoryViewHolder(
            binding,
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position == selectedPosition)
    }

    inner class CategoryViewHolder(
        binding: BookScreenBookBestsellerCatalogCategoryEntryBinding,
        private val onItemClick: (Int) -> Unit,
    ): RecyclerView.ViewHolder(binding.root) {
        private val root = binding.root
        private val name = binding.categoryName
        private val image = binding.categoryImage
        private val resources = itemView.resources

        private lateinit var category: BookGenre

        init {
            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition

                if (previousPosition != selectedPosition) {
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onItemClick(category.ordinal)
                }
            }
        }

        fun bind(category: BookGenre, isSelected: Boolean) {
            this.category = category
            name.text = category.name.lowercase().replaceFirstChar { it.titlecase() }
            image.setImageResource(category.iconRes)

            root.isSelected = isSelected

            if (isSelected) {
                name.setTypeface(null, Typeface.BOLD)
                name.setTextColor(resources.getColor(R.color.book_screen_book_bestseller_catalog_text, null))
                image.alpha = 1.0f
            } else {
                name.setTypeface(null, Typeface.NORMAL)
                name.setTextColor(resources.getColor(android.R.color.darker_gray, null))
                image.alpha = 0.7f
            }
        }
    }

    object CategoryDiffCallback: DiffUtil.ItemCallback<BookGenre>() {
        override fun areItemsTheSame(oldItem: BookGenre, newItem: BookGenre): Boolean =
            oldItem.ordinal == newItem.ordinal

        override fun areContentsTheSame(oldItem: BookGenre, newItem: BookGenre): Boolean =
            oldItem == newItem
    }
}
