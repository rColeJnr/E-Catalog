package com.rick.screen_anime.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_anime.model_jikan.Images
import com.rick.data_anime.model_jikan.Jikan
import com.rick.data_anime.model_jikan.Jpg
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
                FavScreen(jikans = listOf())
            }
        }

        return binding.root
    }

}

@Composable
fun FavScreen(jikans: List<Jikan>) {
    Scaffold() {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(jikans) { jikan ->
                JikanItem(jikan = jikan)
            }
        }
    }
}

@Composable
fun JikanItem(jikan: Jikan) {
    Surface(
        modifier = Modifier
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

@Preview
@Composable
fun JikanItemPrev() {
    FavScreen(jikans = dummyData)
}

private val dummyData = listOf(
    Jikan(
        0,
        "",
        Images(Jpg("", "", "")),
        null,
        "This dude title",
        "anime",
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.0,
        1,
        1,
        1,
        1,
        1,
        "Def got nothing for you, but i'll keep writng so that we have at least on elong text, there's so much typos, and i am so tired i just want to sleep. i love me",
        null,
        null,
        null,
        null,
        null,
    ),
    Jikan(
        0,
        "",
        Images(Jpg("", "", "")),
        null,
        "This another title",
        "anime",
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.0,
        1,
        1,
        1,
        1,
        1,
        "I do not have a synopsis for you",
        null,
        null,
        null,
        null,
        null,
    ),
    Jikan(
        0,
        "",
        Images(Jpg("", "", "")),
        null,
        "This another title",
        "anime",
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.0,
        1,
        1,
        1,
        1,
        1,
        "I may or may not have something for you",
        null,
        null,
        null,
        null,
        null,
    ),
)