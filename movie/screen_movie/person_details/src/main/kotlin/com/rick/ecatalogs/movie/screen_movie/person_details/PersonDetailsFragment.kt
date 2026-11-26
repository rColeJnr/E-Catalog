package com.rick.ecatalogs.movie.screen_movie.person_details

import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.MediaType
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.Person
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.PersonCredit
import com.rick.ecatalogs.movie.screen_movie.common.logScreenView
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import com.rick.ecatalogs.movie.screen_movie.person_details.databinding.MovieScreenMoviePersonDetailsFragmentPersonDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import javax.inject.Inject

/** An actor's photo, facts, biography and best-known movies and series. */
@AndroidEntryPoint
class PersonDetailsFragment : Fragment() {

    private var _binding: MovieScreenMoviePersonDetailsFragmentPersonDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PersonDetailsViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    private var biographyExpanded = false

    @Inject
    lateinit var textTranslator: TextTranslator

    private var biographyTranslation: TranslationToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val duration = resources.getInteger(
            com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_medium
        ).toLong()
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).setDuration(duration)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).setDuration(duration)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MovieScreenMoviePersonDetailsFragmentPersonDetailsBinding
            .inflate(inflater, container, false)
        analyticsHelper.logScreenView("personDetails")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            setNavigationIcon(com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_back_icon)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
        binding.personError.setOnClickListener { viewModel.retry() }
        biographyTranslation = TranslationToggle(
            binding.personBiographyTranslate,
            binding.personBiography,
            textTranslator,
            // The translation may be longer or shorter: re-check "Read more"
            onTextChanged = { if (_binding != null) bindBiographyExpansion() },
        )
        binding.personBiographyToggle.setOnClickListener {
            biographyExpanded = !biographyExpanded
            bindBiographyExpansion()
        }

        val creditsAdapter = PersonCreditsAdapter(::onCreditClick)
        binding.personCredits.adapter = creditsAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.personProgressBar.isVisible = state is PersonUiState.Loading
                    binding.personError.isVisible = state is PersonUiState.Error
                    val person = (state as? PersonUiState.Success)?.person
                    binding.personHeader.isVisible = person != null
                    if (person == null) {
                        binding.personBiographySection.isVisible = false
                        binding.personCreditsSection.isVisible = false
                    } else {
                        bindPerson(person)
                        creditsAdapter.submitList(person.credits)
                    }
                }
            }
        }
    }

    private fun bindPerson(person: Person) = with(binding) {
        personToolbarName.text = person.name
        personName.text = person.name
        personPhoto.contentDescription =
            getString(R.string.movie_screen_movie_person_details_photo, person.name)
        person.profilePath?.let { provideGlide(personPhoto, getTmdbImageUrl(it, size = "w342")) }

        personDepartment.text = person.knownForDepartment
        personDepartment.isVisible = person.knownForDepartment != null

        val birthday = person.birthday?.toLocalDate()
        val deathday = person.deathday?.toLocalDate()
        personBorn.text = birthday?.let {
            if (deathday == null) {
                getString(
                    R.string.movie_screen_movie_person_details_born_age,
                    it.formatted(), Period.between(it, LocalDate.now()).years,
                )
            } else {
                getString(R.string.movie_screen_movie_person_details_born, it.formatted())
            }
        }
        personBorn.isVisible = birthday != null
        personDied.text = if (birthday != null && deathday != null) {
            getString(
                R.string.movie_screen_movie_person_details_died,
                deathday.formatted(), Period.between(birthday, deathday).years,
            )
        } else {
            null
        }
        personDied.isVisible = personDied.text.isNotEmpty()
        personPlaceOfBirth.text = person.placeOfBirth
        personPlaceOfBirth.isVisible = person.placeOfBirth != null

        personBiographySection.isVisible = person.biography.isNotBlank()
        // TMDB biographies are English; the toggle offers the phone's language
        biographyTranslation?.bind(person.biography)

        personCreditsSection.isVisible = person.credits.isNotEmpty()
    }

    private fun bindBiographyExpansion() = with(binding) {
        personBiography.maxLines = if (biographyExpanded) Int.MAX_VALUE else COLLAPSED_BIO_LINES
        personBiographyToggle.setText(
            if (biographyExpanded) R.string.movie_screen_movie_person_details_read_less
            else R.string.movie_screen_movie_person_details_read_more
        )
        // Only offer "Read more" when the text is actually cut off
        personBiography.post {
            if (_binding == null) return@post
            val layout = personBiography.layout ?: return@post
            val truncated = layout.lineCount > 0 &&
                layout.getEllipsisCount(layout.lineCount - 1) > 0
            personBiographyToggle.isVisible = biographyExpanded || truncated
        }
    }

    private fun onCreditClick(credit: PersonCredit) {
        val uri = when (credit.mediaType) {
            MediaType.MOVIE -> "com.rick.ecs://trending_movie_details_fragment/${credit.id}"
            MediaType.SERIES -> "com.rick.ecs://trending_series_details_fragment/${credit.id}"
        }
        findNavController().navigateWithMotion(uri.toUri())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        biographyTranslation = null
        _binding = null
    }
}

private fun String.toLocalDate(): LocalDate? = try {
    LocalDate.parse(this)
} catch (e: DateTimeParseException) {
    null
}

private fun LocalDate.formatted(): String =
    format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

private const val COLLAPSED_BIO_LINES = 6
