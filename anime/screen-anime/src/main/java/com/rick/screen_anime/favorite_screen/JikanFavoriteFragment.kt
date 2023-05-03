package com.rick.screen_anime.favorite_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_anime.favorite.JikanFavorite
import com.rick.data_anime.model_jikan.Images
import com.rick.data_anime.model_jikan.Jikan
import com.rick.data_anime.model_jikan.Jpg
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.FragmentJikanFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JikanFavoriteFragment : Fragment() {

    private var _binding: FragmentJikanFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: JikanFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJikanFavoriteBinding.inflate(inflater, container, false)

        binding.composeView.setContent {

            // Should i have to separate screen??? that would make sense right???
            // I'll probably just mix these two
            val anime = viewModel.anime.observeAsState().value!!
            val manga = viewModel.manga.observeAsState().value!!
            MdcTheme {
                FavScreen(anime = anime, manga = manga, onFavClick = { OnFavClick() })
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

@Composable
fun FavScreen(anime: List<JikanFavorite>, manga: List<JikanFavorite>, onFavClick: (JikanFavorite) -> Unit) {
    Column {
        Scaffold {
            Text(
                "Anime"
            )
            LazyColumn(modifier = Modifier.padding(it)) {
                items(anime) { jikan ->
                    AnimeItem(jikan = jikan)
                    Divider(
                        Modifier.height(1.dp),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Manga"
            )
            LazyColumn(modifier = Modifier.padding(it)) {
                items(manga) { jikan ->
                    MangaItem(jikan = jikan, onFavClick = onFavClick)
                    Divider(
                        Modifier.height(1.dp),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun AnimeItem(jikan: JikanFavorite) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth()) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (title, image) = createRefs()
                Text(
                    text = jikan.title ?: "no title found",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                )
                IconButton(
                    onClick = { OnFavClick() },
                    modifier = Modifier.constrainAs(image) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = stringResource(
                            R.string.favorite
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(jikan.image)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.fav_jikan),
                modifier = Modifier.height(dimensionResource(id = R.dimen.image_height)),
                contentScale = ContentScale.FillHeight,
            )
            Text(
                text = jikan.synopsis
                    ?: ("no synopsis found, maybe i should give u a bit more text, " +
                            "also, i should wrap you and give u eclipses dots"),
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = jikan.rating ?: "unrated rating", style = MaterialTheme.typography.body2)
        }
    }
}

@Composable
fun MangaItem(jikan: JikanFavorite, onFavClick: (JikanFavorite) -> Unit) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth()) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (title, image) = createRefs()
                Text(
                    text = jikan.title ?: "no title found",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                )
                IconButton(
                    onClick = { onFavClick(jikan) },
                    modifier = Modifier.constrainAs(image) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = stringResource(
                            R.string.favorite
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(jikan.image)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.fav_jikan),
                modifier = Modifier.height(dimensionResource(id = R.dimen.image_height)),
                contentScale = ContentScale.FillHeight,
            )
            Text(
                text = jikan.synopsis
                    ?: ("no synopsis found, maybe i should give u a bit more text, " +
                            "also, i should wrap you and give u eclipses dots"),
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = jikan.rating ?: "unrated rating", style = MaterialTheme.typography.body2)
        }
    }
}

private fun OnFavClick() {

}

//@Preview
//@Composable
//fun JikanItemPrev() {
//    FavScreen(anime = dummyData, manga = dummyData, {})
//}

private val dummyData = listOf(
    Jikan(
        0,
        "",
        Images(
            Jpg(
                "https://assets-prd.ignimgs.com/2022/08/17/top25animecharacters-blogroll-1660777571580.jpg",
                "",
                ""
            )
        ),
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
        true
    ),
    Jikan(
        0,
        "",
        Images(
            Jpg(
                "https://cdn.vox-cdn.com/thumbor/xBIBkXiGLcP-kph3pCX61U7RMPY=/0x0:1400x788/1200x800/filters:focal(588x282:812x506)/cdn.vox-cdn.com/uploads/chorus_image/image/70412073/0377c76083423a1414e4001161e0cdffb0b36e1f_760x400.0.png",
                "",
                ""
            )
        ),
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
        false
    ),
    Jikan(
        0,
        "",
        Images(
            Jpg(
                "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
                "",
                ""
            )
        ),
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
        true
    ),
)