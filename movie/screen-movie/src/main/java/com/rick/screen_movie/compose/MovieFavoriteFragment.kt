package com.rick.screen_movie.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_movie.ny_times.Link
import com.rick.data_movie.ny_times.Movie
import com.rick.data_movie.ny_times.Multimedia
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentMovieFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFavoriteFragment : Fragment() {

    private var _binding: FragmentMovieFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieFavoriteBinding.inflate(inflater, container, false)

        binding.compoeView.setContent {
            MdcTheme {
                FavScreen(movies = dummyMovies)
            }
        }

        return binding.root
    }
}

@Composable
fun FavScreen(movies: List<Movie>) {
    Scaffold {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(movies) { movie ->
                MovieItem(movie)
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    Surface {
        Column(
            modifier = Modifier
                .wrapContentHeight(align = Alignment.CenterVertically)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (title, rating, image) = createRefs()
                Text(
                    text = movie.title,
                    modifier = Modifier.constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    })
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(25.dp).constrainAs(image) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    })
                Text(text = movie.rating, modifier = Modifier.constrainAs(rating) {
                    end.linkTo(image.start)
                    top.linkTo(parent.top)
                })
            }
            Spacer(modifier = Modifier.height(2.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.link.url)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.movie_fragment),
                placeholder = painterResource(R.drawable.ic_100tb),
                modifier = Modifier.height(dimensionResource(id = R.dimen.image_height)),
                contentScale = ContentScale.FillHeight,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = movie.summary)
        }
    }
}

@Preview
@Composable
fun MovieFavPreview() {
    MaterialTheme {
        MovieItem(movie = dummyMovies[0])
    }
}

val dummyMovies = listOf(
    Movie(
        0,
        "Movie 1",
        "This is the summarty of movie 1",
        "pg-13",
        "12,45",
        Link("https://media.istockphoto.com/id/1368264124/photo/extreme-close-up-of-thrashing-emerald-ocean-waves.jpg?b=1&s=170667a&w=0&k=20&c=qha_PaU54cu9QCu1UTlORP4-sW0MqLGERkdFKmC06lI="),
        Multimedia("")
    ),
    Movie(
        0,
        "Movie 1",
        "This is the summarty of movie 1",
        "pg-13",
        "12,45",
        Link("https://www.shutterstock.com/image-photo/surreal-image-african-elephant-wearing-260nw-1365289022.jpg"),
        Multimedia("")
    ),
    Movie(
        0,
        "Movie 1",
        "This is the summarty of movie 1",
        "pg-13",
        "12,45",
        Link("https://www.shutterstock.com/image-photo/surreal-image-african-elephant-wearing-260nw-1365289022.jpg"),
        Multimedia("")
    ),
)