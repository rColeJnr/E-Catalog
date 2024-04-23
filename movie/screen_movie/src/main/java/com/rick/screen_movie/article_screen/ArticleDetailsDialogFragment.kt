package com.rick.screen_movie.article_screen

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rick.data.model_movie.UserArticle
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.DialogNymovieDetailsBinding
import com.rick.screen_movie.util.provideGlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleDetailsDialogFragment(
    private val article: UserArticle,
    private val onDialogFavoriteClick: (View, Long, Boolean) -> Unit,
    private val onWebUrlClick: (String) -> Unit
) : DialogFragment() {

//    private lateinit var listener: NYMovieDetailsDialogListener
//
//    interface NYMovieDetailsDialogListener {
//        fun onDialogFavoriteClick(view: View, movie: NYMovie)
//        fun onWebUlrClick(link: String)
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            listener = context as NYMovieDetailsDialogListener
//        } catch (e: ClassCastException) {
//            // The activity doesn't implement the interface
//            throw ClassCastException(("$context must implement NYMovieDetailsDialogListener"))
//        }
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = DialogNymovieDetailsBinding.inflate(it.layoutInflater)
            builder.setView(view.root)
            view.closeDialog.setOnClickListener {
                dialog?.dismiss()
            }
            view.favorite.setOnClickListener { view ->
                onDialogFavoriteClick(view, article.id, article.isFavorite)
            }
            view.source.setOnClickListener {
                onWebUrlClick(article.webUrl)
                dialog?.dismiss()
            }
            val image = article.multimedia[0].url
            if (image.isNotEmpty()) provideGlide(view.image, "https://www.nytimes.com/$image")
            view.title.text = article.headline.main
            view.summary.text = article.leadParagraph
            view.date.text =
                resources.getString(R.string.release_date, article.pubDate.substring(0, 10))
            view.reporter.text = resources.getString(R.string.reporter, article.byline.original)
            view.source.text = resources.getString(R.string.source, article.source)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}