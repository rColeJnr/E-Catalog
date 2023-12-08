package com.rick.auth_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rick.auth_screen.databinding.FragmentAuthenticationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationFragment: Fragment() {

  private var _binding: FragmentAuthenticationBinding? = null
  private val binding get() = _binding!!
  private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

@Composable
fun AuthencticationScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
                .background(color = MaterialTheme.colors.surface) //i would just like to state that i don't remember the last time i wrote compose code.
        ) {
        Text(
            text = "Discover what \nto binge watch \nnext, \nor read!", style = MaterialTheme.typography.body2,
            modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 61.dp,
                                    y = 74.dp)
                        .border(border = BorderStroke(1.dp, color = MaterialTheme.colors.secondary)))
    }
}
