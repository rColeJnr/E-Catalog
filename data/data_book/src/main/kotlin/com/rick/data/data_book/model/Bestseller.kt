package com.rick.data.data_book.model

import com.rick.data.database_book.model.BestsellerEntity
import com.rick.data.network_book.model.BestsellerNetwork

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