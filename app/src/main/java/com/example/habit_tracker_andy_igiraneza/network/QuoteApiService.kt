package com.example.habit_tracker_andy_igiraneza.network

import com.example.habit_tracker_andy_igiraneza.models.Quote
import retrofit2.http.GET

interface QuoteApiService {
    @GET("random")
    suspend fun getRandomQuote(): List<Quote>
}