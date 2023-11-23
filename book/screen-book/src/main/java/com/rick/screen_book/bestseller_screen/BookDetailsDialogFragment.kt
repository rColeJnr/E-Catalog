package com.rick.screen_book.bestseller_screen

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rick.screen_book.databinding.DialogBookDetailsBinding

class BookDetailsDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = DialogBookDetailsBinding.inflate(it.layoutInflater)
            builder.setView(view.root)
            view.closeDialog.setOnClickListener{
                dialog?.dismiss()
            }
            view.favorite.setOnClickListener {
                onDialogFavoriteClick()
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}