//package com.rick.book.screen_book.bestseller_search
//
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.AsyncListDiffer
//import androidx.recyclerview.widget.RecyclerView
//import com.rick.data.model_book.gutenberg.Formats
//import com.rick.screen_book.gutenberg_screen.BookCatalogAdapter
//import com.rick.screen_book.gutenberg_screen.GutenbergCatalogViewHolder
//
//class SearchAdapter(
//    private val onItemClick: (view: View, formats: Formats) -> Unit,
//    private val onFavClick: (Int, Boolean) -> Unit
//) : RecyclerView.Adapter<GutenbergCatalogViewHolder>() {
//
//    private val diffUtil = BookCatalogAdapter.DIFF_UTIL
//
//    val differ = AsyncListDiffer(this, diffUtil)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GutenbergCatalogViewHolder {
//        return GutenbergCatalogViewHolder.create(parent, onItemClick, onFavClick)
//    }
//
//    override fun onBindViewHolder(holder: GutenbergCatalogViewHolder, position: Int) {
//        val book = differ.currentList[position]
//        holder.bind(book)
//    }
//
//    override fun getItemCount(): Int =
//        differ.currentList.size
//}
