package com.example.booktrack.data.service

import com.example.booktrack.BuildConfig
import com.example.booktrack.data.api.GoogleBooksResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class GoogleBooksService {
    
    private val baseUrl = "https://www.googleapis.com/books/v1/volumes"
    private val gson = Gson()
    
    // API key from BuildConfig (set in local.properties)
    private val apiKey: String? = BuildConfig.GOOGLE_BOOKS_API_KEY.takeIf { it.isNotBlank() }
    
    suspend fun searchBooks(query: String, maxResults: Int = 10): Result<GoogleBooksResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val urlString = if (apiKey != null) {
                    "$baseUrl?q=$encodedQuery&maxResults=$maxResults&key=$apiKey"
                } else {
                    "$baseUrl?q=$encodedQuery&maxResults=$maxResults"
                }
                val url = URL(urlString)
                
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        reader.readText()
                    }
                    
                    val booksResponse = gson.fromJson(response, GoogleBooksResponse::class.java)
                    Result.success(booksResponse)
                } else {
                    Result.failure(Exception("HTTP error: $responseCode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
