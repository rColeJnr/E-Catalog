package com.rick.ecatalogs.book.screen_book.gutenberg_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergFilter
import com.rick.ecatalogs.book.screen_book.gutenberg_catalog.databinding.BookScreenBookGutenbergCatalogHeaderBinding

/** Languages offered as filters, by ISO 639-1 code, named in their own language. */
private val LANGUAGES = listOf(
    "en" to "English",
    "pt" to "Português",
    "ru" to "Русский",
    "fr" to "Français",
    "es" to "Español",
    "de" to "Deutsch",
    "it" to "Italiano",
)

/** Topics offered as filters: the Gutendex `topic` value and its label. */
private val TOPICS = listOf(
    "fiction" to R.string.book_screen_book_gutenberg_catalog_topic_fiction,
    "science fiction" to R.string.book_screen_book_gutenberg_catalog_topic_science_fiction,
    "poetry" to R.string.book_screen_book_gutenberg_catalog_topic_poetry,
    "philosophy" to R.string.book_screen_book_gutenberg_catalog_topic_philosophy,
    "history" to R.string.book_screen_book_gutenberg_catalog_topic_history,
    "children" to R.string.book_screen_book_gutenberg_catalog_topic_children,
    "mystery" to R.string.book_screen_book_gutenberg_catalog_topic_mystery,
    "adventure" to R.string.book_screen_book_gutenberg_catalog_topic_adventure,
    "love" to R.string.book_screen_book_gutenberg_catalog_topic_romance,
    "horror" to R.string.book_screen_book_gutenberg_catalog_topic_horror,
)

/**
 * Title, slogan and the language / topic filter chips, as the first item of a ConcatAdapter in
 * front of the book grid.
 */
class GutenbergHeaderAdapter(
    private val onFilterChange: (GutenbergFilter) -> Unit,
) : RecyclerView.Adapter<GutenbergHeaderAdapter.HeaderViewHolder>() {

    var filter: GutenbergFilter = GutenbergFilter()
        set(value) {
            field = value
            boundHolder?.bind(value)
        }

    private var boundHolder: HeaderViewHolder? = null

    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HeaderViewHolder(
        BookScreenBookGutenbergCatalogHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        boundHolder = holder
        holder.bind(filter)
    }

    override fun onViewRecycled(holder: HeaderViewHolder) {
        if (boundHolder === holder) boundHolder = null
    }

    inner class HeaderViewHolder(private val binding: BookScreenBookGutenbergCatalogHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var binding_ = false

        init {
            val context = binding.root.context
            addChips(
                binding.languageChips,
                listOf<Pair<String?, String>>(
                    null to context.getString(R.string.book_screen_book_gutenberg_catalog_all_languages)
                ) + LANGUAGES,
            )
            addChips(
                binding.topicChips,
                listOf<Pair<String?, String>>(
                    null to context.getString(R.string.book_screen_book_gutenberg_catalog_all_topics)
                ) + TOPICS.map { (topic, @StringRes label) -> topic to context.getString(label) },
            )
            binding.languageChips.setOnCheckedStateChangeListener { group, _ ->
                if (binding_) return@setOnCheckedStateChangeListener
                val language = checkedTag(group)
                if (language != filter.language) onFilterChange(filter.copy(language = language))
            }
            binding.topicChips.setOnCheckedStateChangeListener { group, _ ->
                if (binding_) return@setOnCheckedStateChangeListener
                val topic = checkedTag(group)
                if (topic != filter.topic) onFilterChange(filter.copy(topic = topic))
            }
        }

        fun bind(filter: GutenbergFilter) {
            binding_ = true
            check(binding.languageChips, filter.language)
            check(binding.topicChips, filter.topic)
            binding_ = false
        }

        private fun addChips(group: ChipGroup, chips: List<Pair<String?, String>>) {
            chips.forEach { (value, label) ->
                group.addView(
                    Chip(group.context, null, com.google.android.material.R.attr.chipStyle).apply {
                        id = View.generateViewId()
                        text = label
                        tag = value
                        isCheckable = true
                    }
                )
            }
        }

        private fun check(group: ChipGroup, value: String?) {
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip
                chip.isChecked = chip.tag as String? == value
            }
        }

        private fun checkedTag(group: ChipGroup): String? =
            group.findViewById<Chip>(group.checkedChipId)?.tag as String?
    }
}
