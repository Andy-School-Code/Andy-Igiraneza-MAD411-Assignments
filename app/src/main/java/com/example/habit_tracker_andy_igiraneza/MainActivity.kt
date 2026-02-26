package com.example.habit_tracker_andy_igiraneza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.habit_tracker_andy_igiraneza.ui.theme.Habit_Tracker_Andy_IgiranezaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Habit_Tracker_Andy_IgiranezaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HabitTrackerApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class Habit(val name: String, val isCompleted: Boolean = false)

@Composable
fun HabitTrackerApp(modifier: Modifier = Modifier) {
    val habits = remember { mutableStateListOf<Habit>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderSection()

        Spacer(modifier = Modifier.height(16.dp))

        HabitInputSection(
            onAddHabit = { habitName ->
                habits.add(Habit(habitName))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        HabitList(
            habits = habits,
            onCompleteHabit = { index ->
                val currentHabit = habits[index]
                habits[index] = currentHabit.copy(isCompleted = true)
            }
        )
    }
}

@Composable
fun HeaderSection() {
    Text(
        text = "Student Habit Tracker",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun HabitInputSection(onAddHabit: (String) -> Unit) {
    var inputText by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter a new habit") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (inputText.isNotBlank()) {
                    onAddHabit(inputText.trim())
                    inputText = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Habit")
        }
    }
}

@Composable
fun HabitList(
    habits: List<Habit>,
    onCompleteHabit: (Int) -> Unit
) {
    LazyColumn {
        itemsIndexed(habits) { index, habit ->
            HabitItem(
                habit = habit,
                onCompleted = { onCompleteHabit(index) }
            )
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onCompleted: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = habit.name,
            color = if (habit.isCompleted) Color.Gray else Color.Black,
            textDecoration = if (habit.isCompleted) TextDecoration.LineThrough else TextDecoration.None
        )

        Button(
            onClick = { if (!habit.isCompleted) onCompleted() }
        ) {
            Text("Completed")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitTrackerPreview() {
    Habit_Tracker_Andy_IgiranezaTheme {
        HabitTrackerApp()
    }
}