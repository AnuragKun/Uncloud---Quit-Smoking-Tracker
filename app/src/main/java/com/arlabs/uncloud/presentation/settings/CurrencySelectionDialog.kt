package com.arlabs.uncloud.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.arlabs.uncloud.data.repository.CurrencyRepository
import com.arlabs.uncloud.domain.model.AppCurrency

@Composable
fun CurrencySelectionDialog(
    onDismiss: () -> Unit,
    onCurrencySelected: (AppCurrency) -> Unit
) {
    // Search State
    var searchQuery by remember { mutableStateOf("") }

    // Filter Logic using your existing Repository
    val filteredCurrencies = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            CurrencyRepository.worldCurrencies
        } else {
            CurrencyRepository.worldCurrencies.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp) // Taller, fixed height for better scrolling
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(4.dp), // Sharp "System Window" corners
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30363D))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 1. TERMINAL HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT CURRENCY UNIT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFF00E5FF)
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. SEARCH INPUT
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search database...", fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = Color(0xFF00E5FF))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF30363D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF00E5FF),
                        focusedContainerColor = Color(0xFF0D1117),
                        unfocusedContainerColor = Color(0xFF0D1117)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. CURRENCY LIST
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCurrencies) { currency ->
                        CurrencyItem(
                            currency = currency,
                            onClick = { onCurrencySelected(currency) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyItem(currency: AppCurrency, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161B22), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF252A30), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flag
        Text(text = currency.flag, fontSize = 24.sp)

        Spacer(modifier = Modifier.width(16.dp))

        // Code & Name
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${currency.symbol})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF00E5FF), // Cyan accent for symbol
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Text(
                text = currency.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )
        }

        // Selection Indicator
        Text(
            text = ">",
            color = Color(0xFF30363D),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}