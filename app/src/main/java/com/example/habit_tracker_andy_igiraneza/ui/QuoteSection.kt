package com.example.habit_tracker_andy_igiraneza.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuoteSection(
    quoteUiState: QuoteUiState,
    onRefreshQuote: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Daily Inspiration",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        when (quoteUiState) {
            is QuoteUiState.Loading -> {
                CircularProgressIndicator()
            }

            is QuoteUiState.Success -> {
                Text(
                    text = "\"${quoteUiState.quote.q}\"",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "- ${quoteUiState.quote.a}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            is QuoteUiState.Error -> {
                Text(
                    text = quoteUiState.message,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Button(onClick = onRefreshQuote) {
                Text("Refresh Quote")
            }
        }
    }
}