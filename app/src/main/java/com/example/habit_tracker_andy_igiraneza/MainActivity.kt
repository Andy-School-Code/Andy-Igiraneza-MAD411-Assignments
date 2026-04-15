package com.example.habit_tracker_andy_igiraneza

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habit_tracker_andy_igiraneza.ui.QuoteSection
import com.example.habit_tracker_andy_igiraneza.ui.QuoteUiState
import com.example.habit_tracker_andy_igiraneza.ui.theme.Habit_Tracker_Andy_IgiranezaTheme
import com.example.habit_tracker_andy_igiraneza.viewmodel.HabitTrackerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HabitTracker", "onCreate called")
        enableEdgeToEdge()

        setContent {
            Habit_Tracker_Andy_IgiranezaTheme {
                HabitTrackerApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("HabitTracker", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("HabitTracker", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("HabitTracker", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("HabitTracker", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HabitTracker", "onDestroy called")
    }
}

data class Habit(
    val id: Int,
    val name: String,
    val isCompleted: Boolean = false
)

@Composable
fun HabitTrackerApp(
    habitTrackerViewModel: HabitTrackerViewModel = viewModel()
) {
    val navController = rememberNavController()

    val quoteUiState by habitTrackerViewModel.quoteUiState.collectAsStateWithLifecycle()
    val habits = habitTrackerViewModel.habits

    var habitText by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            HabitMainScreen(
                habits = habits,
                habitText = habitText,
                quoteUiState = quoteUiState,
                onRefreshQuote = { habitTrackerViewModel.fetchQuote() },
                onHabitTextChange = { habitText = it },
                onAddHabit = {
                    if (habitText.isNotBlank()) {
                        habitTrackerViewModel.addHabit(habitText)
                        habitText = ""

                        scope.launch {
                            snackbarHostState.showSnackbar("Habit added successfully")
                        }
                    }
                },
                onCompleteHabit = { habit ->
                    habitTrackerViewModel.completeHabit(habit)
                },
                onDeleteHabit = { habit ->
                    habitTrackerViewModel.deleteHabit(habit)
                },
                onViewDetails = { habit ->
                    navController.navigate(
                        "detail/${habit.name.toUri()}/${habit.isCompleted}"
                    )
                },
                snackbarHostState = snackbarHostState
            )
        }

        composable(
            route = "detail/{habitName}/{isCompleted}",
            arguments = listOf(
                navArgument("habitName") { type = NavType.StringType },
                navArgument("isCompleted") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val habitName = backStackEntry.arguments?.getString("habitName") ?: ""
            val isCompleted = backStackEntry.arguments?.getBoolean("isCompleted") ?: false

            HabitDetailScreen(
                habitName = habitName,
                isCompleted = isCompleted,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun HabitMainScreen(
    habits: List<Habit>,
    habitText: String,
    quoteUiState: QuoteUiState,
    onRefreshQuote: () -> Unit,
    onHabitTextChange: (String) -> Unit,
    onAddHabit: () -> Unit,
    onCompleteHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onViewDetails: (Habit) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("habit_prefs", MODE_PRIVATE)
    }

    var displayName by rememberSaveable {
        mutableStateOf(prefs.getString("display_name", "") ?: "")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit
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

            if (displayName.isNotBlank()) {
                Text(
                    text = "Welcome back, $displayName!",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    prefs.edit()
                        .putString("display_name", displayName)
                        .apply()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Name")
            }

            Spacer(modifier = Modifier.height(16.dp))

            QuoteSection(
                quoteUiState = quoteUiState,
                onRefreshQuote = onRefreshQuote
            )

            Spacer(modifier = Modifier.height(16.dp))

            HabitInputSection(
                habitText = habitText,
                onHabitTextChange = onHabitTextChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.stclaircollege.ca".toUri()
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open St. Clair College Website")
            }

            Spacer(modifier = Modifier.height(16.dp))

            HabitList(
                habits = habits,
                onCompleteHabit = onCompleteHabit,
                onDeleteHabit = onDeleteHabit,
                onViewDetails = onViewDetails
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
    onCompleteHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onViewDetails: (Habit) -> Unit
) {
    LazyColumn {
        items(habits) { habit ->
            HabitItem(
                habit = habit,
                onCompleted = { onCompleteHabit(habit) },
                onDelete = { onDeleteHabit(habit) },
                onViewDetails = { onViewDetails(habit) }
            )
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onCompleted: () -> Unit,
    onDelete: () -> Unit,
    onViewDetails: () -> Unit
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
                color = if (habit.isCompleted) Color.Gray else Color.Black,
                textDecoration = if (habit.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
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

            Button(onClick = onDelete) {
                Text("Delete")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = onViewDetails) {
                Text("View Details")
            }
        }
    }
}

@Composable
fun HabitDetailScreen(
    habitName: String,
    isCompleted: Boolean,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Habit Detail",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Habit Name: $habitName",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isCompleted) "Status: Completed" else "Status: Not Completed",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}