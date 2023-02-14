package com.rick.screen_anime.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_anime.model_jikan.Jikan
import com.rick.screen_anime.databinding.FragmentJikanFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JikanFavoriteFragment : Fragment() {

    private var _binding: FragmentJikanFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJikanFavoriteBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            MdcTheme {

            }
        }

        return binding.root
    }

}

@Composable
fun FavScreen() {
    Scaffold() {

    }
}

@Composable
fun JikanItem(jikan: Jikan) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(text = jikan.title ?: "no title found")
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = jikan.synopsis ?: "no synopsis found")
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = jikan.rating ?: "unrated")
        }
    }
}