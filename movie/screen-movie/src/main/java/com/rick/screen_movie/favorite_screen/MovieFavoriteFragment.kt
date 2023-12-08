package com.rick.screen_movie.favorite_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.ny_times_deprecated.Link
import com.rick.data_movie.ny_times_deprecated.Movie
import com.rick.data_movie.ny_times_deprecated.Multimedia
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
    // TODO column bug
    Scaffold {
        Column {
            Text(text = "Movies")
            LazyColumn(modifier = Modifier.padding(it)) {
                items(movies) { movie ->
                    MovieItem(movie)
                    Divider(
                        Modifier.height(1.dp),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
            Text(text = "Series")
            LazyColumn(modifier = Modifier.padding(it)) {
                items(series) { series ->
                    SeriesItem(series)
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
fun FavoriteListItem(favorite: Favorite) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .requiredHeight(82.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colors.surface.copy(alpha = 0.35f))
            .padding(8.dp)
    ) {

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
                modifier = Modifier.height(dimensionResource(id = R.dimen.image_height)),
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

@Preview
@Composable
fun MovieFavPreview() {
    MaterialTheme {
        MovieItem(movie = dummyMovies[0] as Favorite)
    }
}

val dummyMovies = listOf(
    Favorite(
        0,
        "Movie 1",
        "This is the summarty of movie 1",
        "pg-13",
        "https://media.istockphoto.com/id/1368264124/photo/extreme-close-up-of-thrashing-emerald-ocean-waves.jpg?b=1&s=170667a&w=0&k=20&c=qha_PaU54cu9QCu1UTlORP4-sW0MqLGERkdFKmC06lI=",

    ),
    Movie(
        0,
        "Movie 1",
        "This is the summarty of movie 1",
        "pg-13",
        "12,45",
        Link("https://www.shutterstock.com/image-photo/surreal-image-african-elephant-wearing-260nw-1365289022.jpg"),
        Multimedia(""),
        true
    ),
    Movie(
        0,
        "Movie 1",
        "This is the summarty of movie 1",
        "pg-13",
        "12,45",
        Link("https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"),
        Multimedia(""),
        true
    ),
)