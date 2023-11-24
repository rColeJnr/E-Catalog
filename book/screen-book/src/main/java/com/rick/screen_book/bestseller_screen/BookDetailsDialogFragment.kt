package com.rick.screen_book.bestseller_screen

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rick.data_book.nytimes.model.NYBook
import com.rick.screen_book.databinding.DialogBookDetailsBinding
import com.rick.screen_book.provideGlide

class BookDetailsDialogFragment(private val book: NYBook) : DialogFragment() {

    internal lateinit var listener: BookDetailsDialogListener

    interface BookDetailsDialogListener {
        fun onDialogFavoriteClick(view: View, book: NYBook)
        fun onAmazonLinkClick(link: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as BookDetailsDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface
            throw ClassCastException(("$context must implement BookDetailsDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = DialogBookDetailsBinding.inflate(it.layoutInflater)
            builder.setView(view.root)
            view.closeDialog.setOnClickListener {
                dialog?.dismiss()
            }
            view.favorite.setOnClickListener {
                listener.onDialogFavoriteClick(it, book)
            }
            view.amazonLink.setOnClickListener {
                listener.onAmazonLinkClick(book.amazonLink)
            }
            if (book.bookImage.isNotEmpty()) provideGlide(view.image, book.bookImage)
            view.title.text = book.title
            view.author.text = book.author
            view.rank.text = book.rank.toString()
            view.rankLastWeek.text = book.rankLastWeek.toString()
            view.weeksOnList.text = book.weeksOnList.toString()
            view.description.text = book.description

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}