package com.rick.screen_movie.favorite_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_movie.favorite.Favorite
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentMovieFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFavoriteFragment : Fragment() {

    private var _binding: FragmentMovieFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieFavoriteBinding.inflate(inflater, container, false)

        binding.compoeView.setContent {
            val nyMovies = viewModel.nyMovie.observeAsState().value ?: listOf()
            val movies = viewModel.movie.observeAsState().value ?: listOf()
            val series = viewModel.series.observeAsState().value ?: listOf()
            MdcTheme {
                FavScreen(nyMovies = nyMovies, movies = movies, series = series)
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
fun FavScreen(nyMovies: List<Favorite>, movies: List<Favorite>, series: List<Favorite>) {
    Scaffold(
        modifier = Modifier
            .padding(8.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column {
            Text(text = "NY Times movie articles", style = MaterialTheme.typography.h5)
            LazyColumn(modifier = Modifier.padding(it)) {
                items(nyMovies) { movie ->
                    FavoriteListItem(movie, {}, {})
//                    Divider(
//                        Modifier.height(1.dp),
//                        color = MaterialTheme.colors.secondary
//                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Movies", style = MaterialTheme.typography.h5)
            LazyColumn(modifier = Modifier.padding(it)) {
                items(movies) { movie ->
                    FavoriteListItem(movie, {}, {})
                    Divider(
                        Modifier.height(1.dp),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Series", style = MaterialTheme.typography.h5)
            LazyColumn(modifier = Modifier.padding(it)) {
                items(series) { series ->
                    FavoriteListItem(series, {}, {})
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
fun FavoriteListItem(
    favorite: Favorite,
    onItemClick: (Favorite) -> Unit,
    onFavClick: (Favorite) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(82.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colors.surface.copy(alpha = 0.35f))
            .clickable {
                onItemClick(favorite)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(favorite.image)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.favorite_fragment),
                placeholder = painterResource(R.drawable.filled_icon),
                modifier = Modifier.height(dimensionResource(id = R.dimen.image_height)),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.Start)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = favorite.title,
                    style = MaterialTheme.typography.body2,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Text(
                    text = favorite.summary,
                    style = MaterialTheme.typography.body2,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = { onFavClick(favorite) },
                modifier = Modifier
                    .requiredSize(45.dp)
                    .padding(end = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.fav_filled_icon),
                    contentDescription = stringResource(
                        id = R.string.favorite_button
                    ),
                    tint = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewFavoriteItem() {
    FavoriteListItem(favorite = Favorite(
        1,
        "Title Of movie",
        "Summary of favorite",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie"
    ), onItemClick = {}, onFavClick = {})
}

@Preview
@Composable
fun PreviewFavScreen() {
    MaterialTheme{
        FavScreen(
            dummynyMovies, dummymovies, dummyseries
        )
    }
}

@Composable
fun MovieItem(movie: Favorite) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (title, rating, image) = createRefs()
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    })
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.constrainAs(image) {
                        end.linkTo(parent.end)
                        top.linkTo(title.top)
                        bottom.linkTo(title.bottom)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = stringResource(
                            R.string.favorite
                        )
                    )
                }
                Text(
                    text = movie.type,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.constrainAs(rating) {
                        end.linkTo(image.start)
                        top.linkTo(title.top)
                        bottom.linkTo(title.bottom)
                    })
            }
            Spacer(modifier = Modifier.height(2.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.image)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.movie_fragment),
                placeholder = painterResource(R.drawable.filled_icon),
                modifier = Modifier
                    .height(dimensionResource(id = R.dimen.image_height))
                    .align(Alignment.End)
                    .padding(end = 6.dp),
                contentScale = ContentScale.FillHeight,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = movie.summary, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
fun SeriesItem(series: Favorite) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (title, rating, image) = createRefs()
                Text(
                    text = series.title,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    })
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.constrainAs(image) {
                        end.linkTo(parent.end)
                        top.linkTo(title.top)
                        bottom.linkTo(title.bottom)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = stringResource(
                            R.string.favorite
                        )
                    )
                }
                Text(
                    text = series.type,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.constrainAs(rating) {
                        end.linkTo(image.start)
                        top.linkTo(title.top)
                        bottom.linkTo(title.bottom)
                    })
            }
            Spacer(modifier = Modifier.height(2.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(series.image)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.movie_fragment),
                placeholder = painterResource(R.drawable.filled_icon),
                modifier = Modifier.height(dimensionResource(id = R.dimen.image_height)),
                contentScale = ContentScale.FillHeight,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = series.summary, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

//@Preview
//@Composable
//fun MovieFavPreview() {
//    MaterialTheme {
//        MovieItem(movie = dummyMovies[0] as Favorite)
//    }
//}
val dummynyMovies = listOf(
    Favorite(
        1,
        "Movie title",
        "Movie summary",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie"
    ),
    Favorite(
        2,
        "Title Of movie, just to see how it looks when long like extra extra long",
        "Summary of favorite",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie"
    )
)
val dummymovies = listOf(
    Favorite(
        3,
        "Title Of movie",
        "Summary Of movie, just to see how it looks when long like extra extra long",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie"
    ),
    Favorite(
        4,
        "Title Of movie, just to see how",
        "Summary Of movie, just to see how it looks when long like extra extra long",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie"
    )
)
val dummyseries = listOf(
    Favorite(
        5,
        "Title Of movie, just to see how it looks when long like extra extra long",
        "Summary Of movie, just to see how it looks when long like extra extra long",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie"
    ),
    Favorite(
        5,
        "Title Of movie, just to see",
        "Summary Of movie, just to see how it",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie"
    )
)