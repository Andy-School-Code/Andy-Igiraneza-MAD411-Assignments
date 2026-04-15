package com.example.habit_tracker_andy_igiraneza.ui

import com.example.habit_tracker_andy_igiraneza.models.Quote

sealed class QuoteUiState {
    data object Loading : QuoteUiState()
    data class Success(val quote: Quote) : QuoteUiState()
    data class Error(val message: String) : QuoteUiState()
}