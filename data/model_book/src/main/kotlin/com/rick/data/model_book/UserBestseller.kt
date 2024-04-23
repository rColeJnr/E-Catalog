package com.rick.data.model_book

import com.rick.data.model_book.bestseller.Bestseller
import com.rick.data.model_book.bestseller.BuyLink

data class UserBestseller internal constructor(
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
    val buyLinks: List<BuyLink>,
    val isFavorite: Boolean,
) {
    constructor(book: Bestseller, userData: BestsellerUserData) : this(
        id = book.id,
        rank = book.rank,
        rankLastWeek = book.rankLastWeek,
        weeksOnList = book.weeksOnList,
        publisher = book.publisher,
        description = book.description,
        title = book.title,
        author = book.author,
        image = book.image,
        amazonLink = book.amazonLink,
        buyLinks = book.buyLinks,
        isFavorite = book.id in userData.bestsellerFavoriteIds
    )
}

fun List<Bestseller>.mapToUserBestseller(userData: BestsellerUserData): List<UserBestseller> =
    map { UserBestseller(it, userData) }

fun Bestseller.mapToUserBestseller(userData: BestsellerUserData): UserBestseller =
    UserBestseller(this, userData)