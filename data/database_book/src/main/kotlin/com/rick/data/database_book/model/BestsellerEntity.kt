package com.rick.data.database_book.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.data.model_book.bestseller.Bestseller
import com.rick.data.model_book.bestseller.BuyLink

@Entity(tableName = "bestseller_table")
data class BestsellerEntity(
    @PrimaryKey
    val id: String,
    val rank: Int,
    val rankLastWeek: Int,
    val weeksOnList: Int,
    val publisher: String,
    val description: String,
    val title: String,
    val author: String,
    val image: String,
    val amazonLink: String,
    val buyLinks: List<BuyLink>
)

fun BestsellerEntity.asBestseller() = Bestseller(
    id = id,
    rank = rank,
    rankLastWeek = rankLastWeek,
    weeksOnList = weeksOnList,
    publisher = publisher,
    description = description,
    title = title,
    author = author,
    image = image,
    amazonLink = amazonLink,
    buyLinks = buyLinks
)