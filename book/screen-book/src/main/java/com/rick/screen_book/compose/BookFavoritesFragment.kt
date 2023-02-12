package com.rick.screen_book.compose

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_book.model.Author
import com.rick.data_book.model.Book
import com.rick.data_book.model.Formats
import com.rick.screen_book.databinding.FragmentBookFavoriteBinding
import com.rick.screen_book.getListAsString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookFavoritesFragment : Fragment() {

    val _binding: FragmentBookFavoriteBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookFavoriteBinding.inflate(inflater, container, false)

        binding.composeView.setContent{
            MdcTheme {
                FavScreen(books = dummyBooks)
            }
        }

        return binding.root
    }

}

@Composable
fun FavScreen(books: List<Book>) {

    // Maybe some day i'll actually start building this :(
    Scaffold(modifier = Modifier.fillMaxSize()) {
        LazyColumn(Modifier.padding(it)) {
            items(books) { book ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Book(book)
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Outlined.Star, contentDescription = "favorite")
                    }
                }
                Divider()
            }
        }
    }
}

@Composable
fun Book(book: Book) {
    Column (modifier = Modifier.padding(8.dp)) {
        Text(text = book.title)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = getListAsString(book.authors))
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview
@Composable
fun FavScreenPrev() {
    MaterialTheme {
        FavScreen(
            books = dummyBooks
        )
    }
}

val dummyBooks = listOf(
    Book(
        0,
        "Book 1",
        listOf(Author(0, 0, "Richard")),
        listOf(),
        true,
        1000,
        Formats(null, null, null, null, null, null, null, null),
        listOf(),
        "",
        listOf()
    ),
    Book(
        1,
        "Book 2",
        listOf(Author(0, 0, "Richard")),
        listOf(),
        true,
        1000,
        Formats(null, null, null, null, null, null, null, null),
        listOf(),
        "",
        listOf()
    ),
    Book(
        2,
        "Book 3",
        listOf(Author(0, 0, "Richard")),
        listOf(),
        true,
        1000,
        Formats(null, null, null, null, null, null, null, null),
        listOf(),
        "",
        listOf()
    ),
    Book(
        3,
        "Book 4",
        listOf(Author(0, 0, "Richard")),
        listOf(),
        true,
        1000,
        Formats(null, null, null, null, null, null, null, null),
        listOf(),
        "",
        listOf()
    ),
)