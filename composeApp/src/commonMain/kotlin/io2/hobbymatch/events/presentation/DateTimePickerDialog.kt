package io2.hobbymatch.events.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DateTimePickerDialog(
    initialDate: String = "",
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var year by remember { mutableIntStateOf(if (initialDate.isNotEmpty()) initialDate.substring(0, 4).toInt() else 2024) }
    var month by remember { mutableIntStateOf(if (initialDate.isNotEmpty()) initialDate.substring(5, 7).toInt() else 1) }
    var day by remember { mutableIntStateOf(if (initialDate.isNotEmpty()) initialDate.substring(8, 10).toInt() else 1) }
    var hour by remember { mutableIntStateOf(if (initialDate.isNotEmpty()) initialDate.substring(11, 13).toInt() else 12) }
    var minute by remember { mutableIntStateOf(if (initialDate.isNotEmpty()) initialDate.substring(14, 16).toInt() else 0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Wybierz datę i czas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Data (rok, miesiąc, dzień)
                Text(
                    text = "Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Rok
                    NumberPickerColumn(
                        title = "Rok",
                        value = year,
                        range = 2024..2030,
                        onValueChange = { year = it }
                    )
                    
                    // Miesiąc
                    NumberPickerColumn(
                        title = "Miesiąc",
                        value = month,
                        range = 1..12,
                        onValueChange = { month = it },
                        displayTransform = { getMonthName(it) }
                    )
                    
                    // Dzień
                    NumberPickerColumn(
                        title = "Dzień",
                        value = day,
                        range = 1..getDaysInMonth(year, month),
                        onValueChange = { day = it }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Czas (godzina, minuta)
                Text(
                    text = "Czas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Godzina
                    NumberPickerColumn(
                        title = "Godzina",
                        value = hour,
                        range = 0..23,
                        onValueChange = { hour = it },
                        displayTransform = { it.toString().padStart(2, '0') }
                    )
                    
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 24.dp)
                    )
                    
                    // Minuta
                    NumberPickerColumn(
                        title = "Minuta",
                        value = minute,
                        range = 0..59,
                        onValueChange = { minute = it },
                        displayTransform = { it.toString().padStart(2, '0') }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Podsumowanie
                val formattedDate = "${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}T${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:00"
                Text(
                    text = "Wybrana data i czas:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Przyciski
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Anuluj")
                    }
                    
                    TextButton(
                        onClick = {
                            onDateSelected(formattedDate)
                            onDismiss()
                        }
                    ) {
                        Text("Zatwierdź")
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPickerColumn(
    title: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    displayTransform: (Int) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall
        )
        
        Box {
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { expanded = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = displayTransform(value),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                range.forEach { num ->
                    DropdownMenuItem(
                        text = { Text(displayTransform(num)) },
                        onClick = {
                            onValueChange(num)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Styczeń"
        2 -> "Luty"
        3 -> "Marzec"
        4 -> "Kwiecień"
        5 -> "Maj"
        6 -> "Czerwiec"
        7 -> "Lipiec"
        8 -> "Sierpień"
        9 -> "Wrzesień"
        10 -> "Październik"
        11 -> "Listopad"
        12 -> "Grudzień"
        else -> month.toString()
    }
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 31
    }
}

private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}
