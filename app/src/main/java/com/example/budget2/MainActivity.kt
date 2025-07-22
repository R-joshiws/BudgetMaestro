package com.example.budget2

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                FinanceTrackerScreen()
            }
        }
    }

    private fun darkColorScheme() = darkColorScheme(
        primary = Color(0xFF1E3A8A), // Deep Blue
        error = Color(0xFFDC2626),   // Crimson
        onSurface = Color(0xFFFBBF24) // Yellow
    )
}

data class Transaction(val description: String, val amount: Double, val type: String, val category: String, val date: String)

@Composable
fun FinanceTrackerScreen() {
    val context = LocalContext.current
    var income by remember { mutableStateOf("5200.0") }
    var expenses by remember { mutableStateOf("1850.0") }
    var showTransactions by remember { mutableStateOf(false) }
    var showAddTransaction by remember { mutableStateOf(false) }
    var transactions by remember { mutableStateOf(listOf<Transaction>()) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") }
    var category by remember { mutableStateOf("") }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val totalIncome = income.toDoubleOrNull() ?: 0.0
    val totalExpenses = expenses.toDoubleOrNull() ?: 0.0
    val remainingBudget = totalIncome - totalExpenses
    val alertActive = totalExpenses >= totalIncome * 0.8

    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    if (alertActive) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        LaunchedEffect(Unit) {
            vibrator.vibrate(200) // 200ms vibration
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF1E3A8A))
        .padding(16.dp)) {
        Column {
            Text("Finance Tracker", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(modifier = Modifier.weight(1f).padding(4.dp), colors = CardDefaults.cardColors(containerColor = Color.DarkGray)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Income", color = Color(0xFFFFD700), style = MaterialTheme.typography.titleMedium)
                        Text("\$$totalIncome", color = Color(0xFFFFD700), style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Card(modifier = Modifier.weight(1f).padding(4.dp), colors = CardDefaults.cardColors(containerColor = Color.DarkGray)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Expenses", color = Color(0xFFDC2626), style = MaterialTheme.typography.titleMedium)
                        Text("\$$totalExpenses", color = Color(0xFFDC2626), style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Card(modifier = Modifier.weight(1f).padding(4.dp), colors = CardDefaults.cardColors(containerColor = Color.DarkGray)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Remaining", color = if (remainingBudget < 0) Color(0xFFDC2626) else Color(0xFFFBBF24), style = MaterialTheme.typography.titleMedium)
                        Text("\$${remainingBudget}", color = if (remainingBudget < 0) Color(0xFFDC2626) else Color(0xFFFBBF24), style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = alertActive,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFDC2626).copy(alpha = 0.7f))
                        .padding(8.dp)
                ) {
                    Text(
                        "Warning: Expenses at ${"%.1f".format(totalExpenses / totalIncome * 100)}%!",
                        color = Color(0xFFFBBF24).copy(alpha = alpha),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showTransactions = !showTransactions },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Text("Toggle Transactions", color = Color.White)
            }
            AnimatedVisibility(visible = showTransactions) {
                LazyColumn {
                    items(transactions) { transaction ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${transaction.category}: $${transaction.amount}", color = Color.White)
                                Text(transaction.date, color = Color.White)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showAddTransaction = !showAddTransaction },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Text("Add Transaction", color = Color.White)
            }
            AnimatedVisibility(visible = showAddTransaction) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description", color = Color.White) },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFFFBBF24),
                            unfocusedIndicatorColor = Color.Gray,
                            cursorColor = Color(0xFFFBBF24)
                        ),
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount", color = Color.White) },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFFFBBF24),
                            unfocusedIndicatorColor =  Color.Gray,
                            cursorColor = Color(0xFFFBBF24)
                        ),
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("Type (Income/Expense)", color = Color.White) },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFFFBBF24),
                            unfocusedIndicatorColor = Color.Gray,
                            cursorColor = Color(0xFFFBBF24)
                        ),
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category", color = Color.White) },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFFFBBF24),
                            unfocusedIndicatorColor = Color.Gray,
                            cursorColor = Color(0xFFFBBF24)
                        ),
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(
                            onClick = {
                                if (amount.isNotEmpty() && description.isNotEmpty() && type.isNotEmpty() && category.isNotEmpty()) {
                                    val transaction = Transaction(
                                        description = description,
                                        amount = if (type == "Income") amount.toDoubleOrNull() ?: 0.0 else -(amount.toDoubleOrNull() ?: 0.0),
                                        type = type,
                                        category = category,
                                        date = dateFormat.format(Date())
                                    )
                                    transactions = transactions + transaction
                                    income = transactions.filter { it.type == "Income" }.sumOf { it.amount }.toString()
                                    expenses = transactions.filter { it.type == "Expense" }.sumOf { it.amount }.toString()
                                    description = ""
                                    amount = ""
                                    type = "Expense"
                                    category = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)),
                            modifier = Modifier.height(60.dp)
                        ) {
                            Text("Add", color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                description = ""
                                amount = ""
                                type = "Expense"
                                category = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            modifier = Modifier.height(60.dp)
                        ) {
                            Text("Clear", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}