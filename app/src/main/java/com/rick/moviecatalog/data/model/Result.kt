package com.rick.moviecatalog.data.model

data class Result(
    val title: String,
    val summary: String,
    val rating: String,
    val openingDate: String?,
    val link: Link,
    val multimedia: Multimedia,
)
