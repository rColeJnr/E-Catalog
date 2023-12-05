package com.rick.screen_movie.nymovie_screen

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rick.data_movie.ny_times.article_models.NYMovie
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.DialogNymovieDetailsBinding
import com.rick.screen_movie.util.provideGlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NYMovieDetailsDialogFragment(private val movie: NYMovie): DialogFragment() {

    private lateinit var listener: NYMovieDetailsDialogListener

    interface NYMovieDetailsDialogListener {
        fun onDialogFavoriteClick(view: View, movie: NYMovie)
        fun onWebUlrClick(link: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NYMovieDetailsDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface
            throw ClassCastException(("$context must implement NYMovieDetailsDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = DialogNymovieDetailsBinding.inflate(it.layoutInflater)
            builder.setView(view.root)
            view.closeDialog.setOnClickListener {
                dialog?.dismiss()
            }
            view.favorite.setOnClickListener { view ->
                listener.onDialogFavoriteClick(view, movie)
            }
            view.webPage.setOnClickListener {
                listener.onWebUlrClick(movie.webUrl)
            }
            val image = movie.multimedia[0].url
            if (image.isNotEmpty()) provideGlide(view.image, image)
            view.title.text = movie.headline.main
            view.summary.text = movie.summary
            view.date.text = resources.getString(R.string.release_date, movie.pubDate)
            view.reporter.text = resources.getString(R.string.reporter, movie.byline.original)
            view.source.text = resources.getString(R.string.source, movie.source)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}