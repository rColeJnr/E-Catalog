import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.book.screen_book.gutenberg_catalog.GutenbergCatalogViewHolder
import com.rick.book.screen_book.gutenberg_catalog.R
import com.rick.book.screen_book.gutenberg_catalog.databinding.BookScreenBookGutenbergCatalogHeaderBinding
import com.rick.data.model_book.gutenberg.Formats
import com.rick.data.model_book.gutenberg.UserGutenberg

class GutenbergCatalogAdapter(
    private val onItemClick: (view: View, formats: Formats) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit
) : PagingDataAdapter<UserGutenberg, RecyclerView.ViewHolder>(DIFF_UTIL) {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_BOOK
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            GutenbergHeaderViewHolder.create(parent)
        } else {
            GutenbergCatalogViewHolder.create(parent, onItemClick, onFavClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GutenbergHeaderViewHolder -> {
                holder.bind()
            }
            is GutenbergCatalogViewHolder -> {
                val book = getItem(position - 1)
                book?.let { holder.bind(it) }
            }
        }
    }

    override fun getItemCount(): Int {
        val actualCount = super.getItemCount()
        return if (actualCount == 0) 0 else actualCount + 1
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<UserGutenberg>() {
            override fun areItemsTheSame(oldItem: UserGutenberg, newItem: UserGutenberg): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserGutenberg, newItem: UserGutenberg): Boolean =
                oldItem == newItem
        }

        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_BOOK = 1
    }
}

class GutenbergHeaderViewHolder(private val binding: BookScreenBookGutenbergCatalogHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.title.text = itemView.resources.getString(R.string.book_screen_book_gutenberg_catalog_header)
        binding.slogan.text = itemView.resources.getString(R.string.book_screen_book_gutenberg_catalog_slogan)
    }

    companion object {
        fun create(
            parent: ViewGroup,
        ):
                GutenbergHeaderViewHolder {
            val itemBinding = BookScreenBookGutenbergCatalogHeaderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return GutenbergHeaderViewHolder(itemBinding)
        }
    }
}