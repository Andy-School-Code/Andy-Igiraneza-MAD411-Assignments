package com.example.habit_tracker_andy_igiraneza.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_tracker_andy_igiraneza.Habit
import com.example.habit_tracker_andy_igiraneza.models.Quote
import com.example.habit_tracker_andy_igiraneza.network.RetrofitInstance
import com.example.habit_tracker_andy_igiraneza.ui.QuoteUiState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HabitTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val gson = Gson()
    private val fileName = "habits.json"

    val habits = mutableStateListOf<Habit>()

    private val _quoteUiState = MutableStateFlow<QuoteUiState>(QuoteUiState.Loading)
    val quoteUiState: StateFlow<QuoteUiState> = _quoteUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val savedHabits = loadHabitsFromFile()
            habits.clear()
            habits.addAll(savedHabits)
            fetchQuote()
        }
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

    fun addHabit(name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            val nextId = (habits.maxOfOrNull { it.id } ?: 0) + 1

            habits.add(
                Habit(
                    id = nextId,
                    name = name.trim()
                )
            )

            saveHabitsToFile()
        }
    }

    fun completeHabit(habit: Habit) {
        viewModelScope.launch {
            val index = habits.indexOfFirst { it.id == habit.id }

            if (index != -1) {
                habits[index] = habits[index].copy(isCompleted = true)
                saveHabitsToFile()
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habits.removeAll { it.id == habit.id }
            saveHabitsToFile()
        }
    }

    private suspend fun saveHabitsToFile() {
        withContext(Dispatchers.IO) {
            val file = File(getApplication<Application>().filesDir, fileName)
            val json = gson.toJson(habits.toList())
            file.writeText(json)
        }
    }

    private suspend fun loadHabitsFromFile(): List<Habit> {
        return withContext(Dispatchers.IO) {
            val file = File(getApplication<Application>().filesDir, fileName)

            if (!file.exists()) {
                emptyList()
            } else {
                val json = file.readText()
                val type = object : TypeToken<List<Habit>>() {}.type
                gson.fromJson<List<Habit>>(json, type) ?: emptyList()
            }
        }
    }
}