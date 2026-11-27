package com.rick.ecatalogs.movie.screen_movie.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.CastMember
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.TmdbDetailsExtras
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.WatchProvider
import com.rick.ecatalogs.movie.screen_movie.common.databinding.MovieScreenMovieCommonCastEntryBinding
import com.rick.ecatalogs.movie.screen_movie.common.databinding.MovieScreenMovieCommonDetailsExtrasBinding
import com.rick.ecatalogs.movie.screen_movie.common.databinding.MovieScreenMovieCommonProviderEntryBinding
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import java.util.Locale

/**
 * Binds the shared "extras" block of the movie and series details screens: age rating, trailer
 * button, cast row and where-to-watch row. Each part is hidden when TMDB has nothing for it.
 * Watch providers and age rating are for the user's country, falling back to the US.
 * "Watch trailer" opens the trailer screen (the video plays in the app); [title] is shown on it.
 * Tapping a cast member calls [onCastClick] (the screens open the actor screen).
 */
fun MovieScreenMovieCommonDetailsExtrasBinding.bindExtras(
    extras: TmdbDetailsExtras,
    title: String,
    onCastClick: (CastMember) -> Unit,
) {
    val context = root.context
    val country = userCountry(extras)

    // Age rating + trailer
    val ageRating = extras.ageRatings[country] ?: extras.ageRatings[FALLBACK_COUNTRY]
    extrasAgeRating.text = ageRating
    extrasAgeRating.isVisible = !ageRating.isNullOrBlank()

    // Cast
    val castAdapter = extrasCastList.adapter as? CastAdapter
        ?: CastAdapter().also { extrasCastList.adapter = it }
    castAdapter.onClick = onCastClick
    castAdapter.submitList(extras.cast)
    extrasCastSection.isVisible = extras.cast.isNotEmpty()

    // Where to watch: streaming services first; otherwise where to rent or buy
    val providers = extras.watchProviders[country] ?: extras.watchProviders[FALLBACK_COUNTRY]
    val streaming = providers?.stream.orEmpty()
    val rentOrBuy = (providers?.rent.orEmpty() + providers?.buy.orEmpty()).distinctBy { it.id }
    val shown = streaming.ifEmpty { rentOrBuy }
    extrasProvidersKind.setText(
        if (streaming.isNotEmpty()) R.string.movie_screen_movie_common_stream
        else R.string.movie_screen_movie_common_rent_or_buy
    )
    val link = providers?.link
    val providerAdapter = extrasProvidersList.adapter as? ProviderAdapter
        ?: ProviderAdapter().also { extrasProvidersList.adapter = it }
    providerAdapter.onClick = { link?.let(context::openUrl) }
    providerAdapter.submitList(shown)
    extrasProvidersSection.isVisible = shown.isNotEmpty()
}

private fun userCountry(extras: TmdbDetailsExtras): String =
    Locale.getDefault().country.uppercase().takeIf {
        it.isNotBlank() && (it in extras.watchProviders || it in extras.ageRatings)
    } ?: FALLBACK_COUNTRY

private fun Context.openUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: ActivityNotFoundException) {
        // No browser or YouTube app; nothing to open it with
    }
}

private const val FALLBACK_COUNTRY = "US"

private class CastAdapter : ListAdapter<CastMember, CastAdapter.ViewHolder>(DIFF) {

    var onClick: (CastMember) -> Unit = {}

    class ViewHolder(val binding: MovieScreenMovieCommonCastEntryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MovieScreenMovieCommonCastEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = getItem(position)
        with(holder.binding) {
            root.setOnClickListener { onClick(member) }
            castName.text = member.name
            castCharacter.text = member.character
            castCharacter.isVisible = member.character.isNotBlank()
            castPhoto.contentDescription = root.context.getString(
                R.string.movie_screen_movie_common_cast_photo, member.name
            )
            val photo = member.profilePath
            if (photo.isNullOrBlank()) {
                castPhoto.setImageResource(R.drawable.movie_screen_movie_common_ic_person)
            } else {
                provideGlide(castPhoto, getTmdbImageUrl(photo, size = "w185"))
            }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<CastMember>() {
            override fun areItemsTheSame(old: CastMember, new: CastMember) =
                old.id == new.id && old.character == new.character

            override fun areContentsTheSame(old: CastMember, new: CastMember) = old == new
        }
    }
}

private class ProviderAdapter : ListAdapter<WatchProvider, ProviderAdapter.ViewHolder>(DIFF) {

    var onClick: () -> Unit = {}

    class ViewHolder(val binding: MovieScreenMovieCommonProviderEntryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MovieScreenMovieCommonProviderEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val provider = getItem(position)
        with(holder.binding.providerLogo) {
            contentDescription = provider.name
            provider.logoPath?.let { provideGlide(this, getTmdbImageUrl(it, size = "w92")) }
            setOnClickListener { onClick() }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<WatchProvider>() {
            override fun areItemsTheSame(old: WatchProvider, new: WatchProvider) = old.id == new.id
            override fun areContentsTheSame(old: WatchProvider, new: WatchProvider) = old == new
        }
    }
}

/** Deep link to the actor screen (movie/screen_movie/person_details). */
fun personDetailsUri(personId: Int): Uri = Uri.parse("com.rick.ecs://person_details_fragment/$personId")
