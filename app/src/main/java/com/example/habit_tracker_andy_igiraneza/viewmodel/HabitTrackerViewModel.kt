package com.example.habit_tracker_andy_igiraneza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_tracker_andy_igiraneza.models.Quote
import com.example.habit_tracker_andy_igiraneza.network.RetrofitInstance
import com.example.habit_tracker_andy_igiraneza.ui.QuoteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitTrackerViewModel : ViewModel() {

    private val _quoteUiState = MutableStateFlow<QuoteUiState>(QuoteUiState.Loading)
    val quoteUiState: StateFlow<QuoteUiState> = _quoteUiState.asStateFlow()

    init {
        fetchQuote()
    }

    fun fetchQuote() {
        viewModelScope.launch {
            _quoteUiState.value = QuoteUiState.Loading

            try {
                val result: List<Quote> = RetrofitInstance.api.getRandomQuote()
                val quote = result.firstOrNull()

                if (quote != null) {
                    _quoteUiState.value = QuoteUiState.Success(quote)
                } else {
                    _quoteUiState.value = QuoteUiState.Error("No quote returned.")
                }
            } catch (e: Exception) {
                _quoteUiState.value = QuoteUiState.Error("Failed to load quote.")
            }
        }
    }
}
