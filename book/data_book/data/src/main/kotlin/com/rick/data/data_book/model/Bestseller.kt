package com.rick.data.data_book.model

import com.rick.book.data_book.network.model.BestsellerNetwork
import com.rick.data.database_book.model.BestsellerEntity

fun BestsellerNetwork.asBestsellerEntity(): BestsellerEntity = BestsellerEntity(
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
    buyLinks = buyLinks
)