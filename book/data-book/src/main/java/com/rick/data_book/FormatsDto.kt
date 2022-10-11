package com.rick.data_book

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class FormatsDto(
    @SerializedName("application/epub+zip")
    val applicationepubzip: String,
    @SerializedName("application/octet-stream")
    val applicationoctetStream: String?,
    @SerializedName("application/rdf+xml")
    val applicationrdfxml: String,
    @SerializedName("application/x-mobipocket-ebook")
    val applicationxMobipocketEbook: String,
    @SerializedName("image/jpeg")
    val imagejpeg: String,
    @SerializedName("text/html")
    val texthtml: String,
    @SerializedName("text/html; charset=iso-8859-1")
    val texthtmlCharsetiso88591: String?,
    @SerializedName("text/html; charset=utf-8")
    val texthtmlCharsetutf8: String?,
    @SerializedName("text/plain")
    val textplain: String?,
    @SerializedName("text/plain; charset=us-ascii")
    val textplainCharsetusAscii: String?,
    @SerializedName("text/plain; charset=utf-8")
    val textplainCharsetutf8: String?
)

@Parcelize
data class Formats(
    @SerializedName("image/jpeg")
    val image: String,
    @SerializedName("text/html")
    val bookLink: String,
): Parcelable