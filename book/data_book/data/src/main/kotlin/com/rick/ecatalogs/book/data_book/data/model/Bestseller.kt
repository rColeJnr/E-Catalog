package com.rick.ecatalogs.book.data_book.data.model

import com.rick.ecatalogs.book.data_book.network.model.BestsellerNetwork
import com.rick.ecatalogs.book.data_book.database.model.BestsellerEntity

fun BestsellerNetwork.asBestsellerEntity(genre: String): BestsellerEntity = BestsellerEntity(
    id = id,
    rank = rank,
    rankLastWeek = rankLastWeek,
    weeksOnList = weeksOnList,
    publisher = publisher,
    description = description,
    title = title,
    author = author,
    image = image,
    amazonLink = amazonUrl,
    buyLinks = buyLinks ?: emptyList(),
    genre = genre,
)