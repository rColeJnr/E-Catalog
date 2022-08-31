package com.rick.screen_movie.search_screen

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentSearchBinding

class SearchFragment: Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

//        // Get input text
//        val inputText = outlinedTextField.editText?.text.toString()
//
//        outlinedTextField.editText?.doOnTextChanged { inputText, _, _, _ ->
//            // Respond to input text change
//        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        // set menu items visibility
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}