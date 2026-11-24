package com.rick.ecatalogs.movie.screen_movie.person_details

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
import com.rick.ecatalogs.movie.screen_movie.common.TranslationEvent
import com.rick.ecatalogs.movie.screen_movie.common.TranslationViewModel
import com.rick.ecatalogs.movie.screen_movie.common.logScreenView
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import com.rick.ecatalogs.movie.screen_movie.person_details.databinding.MovieScreenMoviePersonDetailsFragmentPersonDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject

/** An actor's photo, facts, biography and best-known movies and series. */
@AndroidEntryPoint
class PersonDetailsFragment : Fragment() {

    private var _binding: MovieScreenMoviePersonDetailsFragmentPersonDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PersonDetailsViewModel by viewModels()
    private val translationViewModel: TranslationViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    private var biographyExpanded = false

    // Biography translation: fetched once on first tap, then toggled locally
    private var originalBiography = ""
    private var translatedBiography: String? = null
    private var showingTranslation = false
    private var translating = false

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
        if (translationViewModel.location.value.isEmpty()) {
            translationViewModel.setLocation(Locale.getDefault().language)
        }
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
        binding.personBiographyTranslate.setOnClickListener { toggleTranslation() }
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

        if (person.biography != originalBiography) {
            // A different person (or first load): drop any translation of the previous text
            originalBiography = person.biography
            translatedBiography = null
            showingTranslation = false
        }
        personBiography.text =
            if (showingTranslation) translatedBiography ?: originalBiography else originalBiography
        personBiographySection.isVisible = person.biography.isNotBlank()
        // TMDB biographies are English; offer a translation to the phone's language
        personBiographyTranslate.isVisible =
            translationViewModel.location.value.lowercase() != "en"
        bindTranslateLabel()
        bindBiographyExpansion()

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

    private fun toggleTranslation() {
        if (translating) return
        val translated = translatedBiography
        when {
            showingTranslation -> showBiography(originalBiography, translated = false)
            translated != null -> showBiography(translated, translated = true)
            else -> {
                translating = true
                translationViewModel.onEvent(
                    TranslationEvent.GetTranslation(
                        texts = listOf(originalBiography),
                        lCode = translationViewModel.location.value,
                    )
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = try {
                        translationViewModel.translation.first { it.isNotEmpty() }
                            .first().text
                    } catch (e: Exception) {
                        null // no translation (offline, or the service failed)
                    } finally {
                        translating = false
                    }
                    if (result != null && _binding != null) {
                        translatedBiography = result
                        showBiography(result, translated = true)
                    }
                }
            }
        }
    }

    /** Cross-fades the biography to [text] and updates the translate action's label. */
    private fun showBiography(text: String, translated: Boolean) {
        val bio = binding.personBiography
        bio.animate().alpha(0f).setDuration(FADE_MILLIS).withEndAction {
            if (_binding == null) return@withEndAction
            bio.text = text
            showingTranslation = translated
            bindTranslateLabel()
            bindBiographyExpansion()
            bio.animate().alpha(1f).setDuration(FADE_MILLIS).start()
        }.start()
    }

    private fun bindTranslateLabel() {
        binding.personBiographyTranslate.setText(
            if (showingTranslation) R.string.movie_screen_movie_person_details_show_original
            else R.string.movie_screen_movie_person_details_show_translation
        )
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
private const val FADE_MILLIS = 200L
