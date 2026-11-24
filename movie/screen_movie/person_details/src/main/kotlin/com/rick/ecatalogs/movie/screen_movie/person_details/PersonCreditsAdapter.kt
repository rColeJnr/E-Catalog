package com.rick.ecatalogs.movie.screen_movie.person_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.MediaType
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.PersonCredit
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import com.rick.ecatalogs.movie.screen_movie.person_details.databinding.MovieScreenMoviePersonDetailsCreditEntryBinding

/** The actor's "Known for" grid. */
class PersonCreditsAdapter(
    private val onClick: (PersonCredit) -> Unit,
) : ListAdapter<PersonCredit, PersonCreditsAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: MovieScreenMoviePersonDetailsCreditEntryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MovieScreenMoviePersonDetailsCreditEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val credit = getItem(position)
        with(holder.binding) {
            creditTitle.text = credit.title
            // "Character · 2024", or "Series · 2019" when the character is unknown
            val role = credit.character.ifBlank {
                if (credit.mediaType == MediaType.SERIES) {
                    root.context.getString(R.string.movie_screen_movie_person_details_series)
                } else {
                    ""
                }
            }
            creditSubtitle.text = listOfNotNull(role.ifBlank { null }, credit.year)
                .joinToString(" · ")
            credit.posterPath?.let { provideGlide(creditPoster, getTmdbImageUrl(it, size = "w342")) }
            root.setOnClickListener { onClick(credit) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<PersonCredit>() {
            override fun areItemsTheSame(old: PersonCredit, new: PersonCredit) =
                old.mediaType == new.mediaType && old.id == new.id

            override fun areContentsTheSame(old: PersonCredit, new: PersonCredit) = old == new
        }
    }
}
