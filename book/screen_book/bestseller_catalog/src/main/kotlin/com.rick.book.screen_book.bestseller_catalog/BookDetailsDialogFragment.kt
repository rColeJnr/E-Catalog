package com.rick.book.screen_book.bestseller_catalog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rick.book.screen_book.bestseller_catalog.databinding.BookScreenBookBestsellerCatalogDialogBookDetailsBinding
import com.rick.data.model_book.bestseller.UserBestseller
import com.rick.data.ui_components.common.provideGlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailsDialogFragment(
    private val book: UserBestseller,
    private val onDialogFavoriteClick: (View, UserBestseller) -> Unit,
    private val onAmazonLinkClick: (String) -> Unit
) : DialogFragment() {

//    private lateinit var listener: BookDetailsDialogListener

//    interface BookDetailsDialogListener {
//        fun onDialogFavoriteClick(view: View, book: NYBook)
//        fun onAmazonLinkClick(link: String)
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            listener = context as BookDetailsDialogListener
//        } catch (e: ClassCastException) {
//            // The activity doesn't implement the interface
//            throw ClassCastException(("$context must implement BookDetailsDialogListener"))
//        }
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view =
                BookScreenBookBestsellerCatalogDialogBookDetailsBinding.inflate(it.layoutInflater)
            builder.setView(view.root)
            view.closeDialog.setOnClickListener {
                dismiss()
            }
            view.favorite.setOnClickListener { view ->
                onDialogFavoriteClick(view, book)
            }
            view.amazonLink.setOnClickListener {
                onAmazonLinkClick(book.amazonLink)
                dismiss()
            }
            if (book.image.isNotEmpty()) provideGlide(view.image, book.image)
            view.title.text = book.title
            view.author.text = book.author
            view.rank.text = resources.getString(R.string.rank, book.rank)
            view.rankLastWeek.text = resources.getString(R.string.rankLastWeek, book.rankLastWeek)
            view.weeksOnList.text = resources.getString(R.string.weeksOnList, book.weeksOnList)
            view.summary.text = book.description

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}