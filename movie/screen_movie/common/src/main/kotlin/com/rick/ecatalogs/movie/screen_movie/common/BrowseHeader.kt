package com.rick.ecatalogs.movie.screen_movie.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.rick.ecatalogs.movie.screen_movie.common.databinding.MovieScreenMovieCommonBrowseHeaderBinding
import com.rick.ecatalogs.movie.screen_movie.common.databinding.MovieScreenMovieCommonBrowseRowEntryBinding
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide

/** A poster in a browse row. [reminderSet] is null when the item has no reminder bell. */
data class BrowseItem(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val subtitle: String?,
    val reminderSet: Boolean? = null,
)

data class BrowseRow(val title: String, val items: List<BrowseItem>)

data class BrowseGenre(val id: Int, val name: String)

data class BrowseHeaderState(
    /** Up to two rows; empty rows are hidden. */
    val rows: List<BrowseRow> = emptyList(),
    val genres: List<BrowseGenre> = emptyList(),
    /** null = "All" (the trending list). */
    val selectedGenreId: Int? = null,
)

/**
 * The header of the movies / series lists (title, browse rows, genre chips), as the first item
 * of a ConcatAdapter in front of the paged list. Updating [state] rebinds the header in place,
 * so the rows keep their scroll position.
 */
class BrowseHeaderAdapter(
    private val title: String,
    private val slogan: String,
    private val allGenresLabel: String,
    private val onItemClick: (BrowseItem) -> Unit,
    private val onBellClick: (BrowseItem) -> Unit,
    private val onGenreSelected: (Int?) -> Unit,
) : RecyclerView.Adapter<BrowseHeaderAdapter.HeaderViewHolder>() {

    var state: BrowseHeaderState = BrowseHeaderState()
        set(value) {
            field = value
            boundHolder?.bind(value)
        }

    private var boundHolder: HeaderViewHolder? = null

    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HeaderViewHolder(
        MovieScreenMovieCommonBrowseHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        boundHolder = holder
        holder.bind(state)
    }

    override fun onViewRecycled(holder: HeaderViewHolder) {
        if (boundHolder === holder) boundHolder = null
    }

    inner class HeaderViewHolder(private val binding: MovieScreenMovieCommonBrowseHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val row1 = BrowseRowAdapter(onItemClick, onBellClick)
        private val row2 = BrowseRowAdapter(onItemClick, onBellClick)
        private var shownGenres: List<BrowseGenre>? = null
        private var bindingChips = false

        init {
            binding.browseHeaderTitle.text = title
            binding.browseHeaderSlogan.text = slogan
            binding.browseRow1.adapter = row1
            binding.browseRow2.adapter = row2
            binding.browseGenres.setOnCheckedStateChangeListener { group, checkedIds ->
                if (bindingChips) return@setOnCheckedStateChangeListener
                val chip = checkedIds.firstOrNull()?.let { group.findViewById<Chip>(it) }
                    ?: return@setOnCheckedStateChangeListener
                val genreId = chip.tag as Int?
                if (genreId != state.selectedGenreId) onGenreSelected(genreId)
            }
        }

        fun bind(state: BrowseHeaderState) = with(binding) {
            bindRow(state.rows.getOrNull(0), browseRow1Section, browseRow1Title, row1)
            bindRow(state.rows.getOrNull(1), browseRow2Section, browseRow2Title, row2)
            bindGenres(state)
        }

        private fun bindRow(
            row: BrowseRow?,
            section: ViewGroup,
            title: android.widget.TextView,
            adapter: BrowseRowAdapter,
        ) {
            section.isVisible = row != null && row.items.isNotEmpty()
            title.text = row?.title
            adapter.submitList(row?.items.orEmpty())
        }

        private fun bindGenres(state: BrowseHeaderState) = with(binding) {
            browseGenresScroll.isVisible = state.genres.isNotEmpty()
            bindingChips = true
            if (state.genres != shownGenres) {
                browseGenres.removeAllViews()
                val chips = listOf<Pair<Int?, String>>(null to allGenresLabel) +
                    state.genres.map { it.id to it.name }
                chips.forEach { (id, name) ->
                    browseGenres.addView(
                        Chip(root.context, null, com.google.android.material.R.attr.chipStyle)
                            .apply {
                                this.id = android.view.View.generateViewId()
                                text = name
                                tag = id
                                isCheckable = true
                            }
                    )
                }
                shownGenres = state.genres
            }
            for (i in 0 until browseGenres.childCount) {
                val chip = browseGenres.getChildAt(i) as Chip
                chip.isChecked = chip.tag as Int? == state.selectedGenreId
            }
            bindingChips = false
        }
    }
}

/** A horizontal row of [BrowseItem] posters (also used for a series' seasons). */
class BrowseRowAdapter(
    private val onItemClick: (BrowseItem) -> Unit,
    private val onBellClick: (BrowseItem) -> Unit = {},
) : ListAdapter<BrowseItem, BrowseRowAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: MovieScreenMovieCommonBrowseRowEntryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MovieScreenMovieCommonBrowseRowEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            root.setOnClickListener { onItemClick(item) }
            browseTitle.text = item.title
            browseSubtitle.text = item.subtitle
            browseSubtitle.isVisible = !item.subtitle.isNullOrBlank()
            item.posterPath?.let { provideGlide(browsePoster, getTmdbImageUrl(it, size = "w342")) }
                ?: browsePoster.setImageDrawable(null)
            browseBell.isVisible = item.reminderSet != null
            browseBell.setImageResource(
                if (item.reminderSet == true) R.drawable.movie_screen_movie_common_ic_bell_active
                else R.drawable.movie_screen_movie_common_ic_bell
            )
            browseBell.setOnClickListener { onBellClick(item) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<BrowseItem>() {
            override fun areItemsTheSame(old: BrowseItem, new: BrowseItem) = old.id == new.id
            override fun areContentsTheSame(old: BrowseItem, new: BrowseItem) = old == new
        }
    }
}
