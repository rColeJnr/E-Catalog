package com.rick.screen_book.favorite_screen

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_book.favorite.Favorite
import com.rick.screen_book.R
import com.rick.screen_book.databinding.FragmentBookFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookFavoritesFragment : Fragment() {

    private var _binding: FragmentBookFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookFavoriteBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            val books = viewModel.books.observeAsState()
            MdcTheme {
                books.value?.let {
                    FavScreen(favorites = it)
                }
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
fun FavScreen(favorites: List<Favorite>) {

    Scaffold(
        modifier = Modifier
            .padding(8.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column {
            Text(text = "Favorites", style = MaterialTheme.typography.h5)
            LazyColumn(modifier = Modifier.padding(it)) {
                items(favorites) { favorite ->
                    FavoriteListItem(favorite, {}, {})
//                    Divider(
//                        Modifier.height(1.dp),
//                        color = MaterialTheme.colors.secondary
//                    )
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
                modifier = Modifier.height(250.dp),
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
                    text = favorite.author,
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
fun PreviewBookItem() {
    FavoriteListItem(favorite = Favorite(
        1,
        "Book title",
        "Book author",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ), onItemClick = {}, onFavClick = {})
}

@Preview
@Composable
fun FavScreenPrev() {
    MaterialTheme {
        FavScreen(
            favorites = dummyBooks
        )
    }
}

val dummyBooks = listOf(
    Favorite(
        1,
        "Book title",
        "Book author",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ),
    Favorite(
        1,
        "Book title",
        "Book author",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ),
    Favorite(
        1,
        "Book title",
        "Book author",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ),
    Favorite(
        1,
        "Book title",
        "Book author",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ),
)