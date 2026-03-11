package com.example.habit_tracker_andy_igiraneza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.habit_tracker_andy_igiraneza.ui.theme.Habit_Tracker_Andy_IgiranezaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Habit_Tracker_Andy_IgiranezaTheme {
                HabitTrackerApp()
            }
        }
    }
}

data class Habit(
    val id: Int,
    val name: String,
    val isCompleted: Boolean = false
)

@Composable
fun HabitTrackerApp() {

    val habits = remember { mutableStateListOf<Habit>() }
    var nextId by remember { mutableStateOf(1) }

    var habitText by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                    if (habitText.isNotBlank()) {

                        habits.add(
                            Habit(
                                id = nextId,
                                name = habitText.trim()
                            )
                        )

                        nextId++
                        habitText = ""

                        scope.launch {
                            snackbarHostState.showSnackbar("Habit added successfully")
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            HeaderSection()

            Spacer(modifier = Modifier.height(16.dp))

            HabitInputSection(
                habitText = habitText,
                onHabitTextChange = { habitText = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HabitList(
                habits = habits,
                onCompleteHabit = { index ->

                    val currentHabit = habits[index]

                    habits[index] = currentHabit.copy(
                        isCompleted = true
                    )
                },
                onDeleteHabit = { index ->
                    habits.removeAt(index)
                }
            )
        }
    }
}

@Composable
fun HeaderSection() {

    Text(
        text = "Student Habit Tracker",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun HabitInputSection(
    habitText: String,
    onHabitTextChange: (String) -> Unit
) {

    OutlinedTextField(
        value = habitText,
        onValueChange = onHabitTextChange,
        label = { Text("Enter a new habit") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun HabitList(
    habits: List<Habit>,
    onCompleteHabit: (Int) -> Unit,
    onDeleteHabit: (Int) -> Unit
) {

    LazyColumn {

        itemsIndexed(habits) { index, habit ->

            HabitItem(
                habit = habit,
                onCompleted = { onCompleteHabit(index) },
                onDelete = { onDeleteHabit(index) }
            )
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onCompleted: () -> Unit,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "ID: ${habit.id}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Text(
                text = habit.name,

                color = if (habit.isCompleted)
                    Color.Gray
                else
                    Color.Black,

                textDecoration = if (habit.isCompleted)
                    TextDecoration.LineThrough
                else
                    TextDecoration.None
            )
        }

        Row {

            Button(
                onClick = {
                    if (!habit.isCompleted) {
                        onCompleted()
                    }
                }
            ) {
                Text("Completed")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onDelete
            ) {
                Text("Delete")
            }
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