package com.example.booktrack.data.api

import com.google.gson.annotations.SerializedName

data class GoogleBooksResponse(
    val items: List<GoogleBookItem>? = null
)

data class GoogleBookItem(
    val id: String,
    val volumeInfo: GoogleBookVolumeInfo
)

data class GoogleBookVolumeInfo(
    val title: String?,
    val authors: List<String>? = null,
    val pageCount: Int? = null,
    val imageLinks: GoogleBookImageLinks? = null,
    val description: String? = null
)

data class GoogleBookImageLinks(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)
