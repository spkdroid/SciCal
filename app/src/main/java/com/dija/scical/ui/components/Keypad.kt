package com.dija.scical.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dija.scical.ui.screen.CalculatorEvent

private data class Key(
    val label: String,
    val event: CalculatorEvent,
    val bg: androidx.compose.ui.graphics.Color
)

@Composable
fun Keypad(
    onEvent: (CalculatorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val numbers      = MaterialTheme.colorScheme.surfaceVariant
    val operators    = MaterialTheme.colorScheme.secondary
    val functions    = MaterialTheme.colorScheme.tertiary
    val bgSurface    = MaterialTheme.colorScheme.surface

    val rows = listOf(
        listOf("7","8","9","+"),
        listOf("4","5","6","-"),
        listOf("1","2","3","*"),
        listOf("0",".","=","/"),
        listOf("("," )","sin","cos"),
        listOf("log","ln","√","^")
    ).map { row ->
        row.map { label ->
            when (label) {
                in "0".."9", "." ->
                    Key(label, CalculatorEvent.Digit(label[0]), numbers)
                "+", "-", "*", "/", "^" ->
                    Key(label, CalculatorEvent.Operator(label), operators)
                "(", ")" ->
                    Key(label,
                        if (label == "(") CalculatorEvent.OpenParen else CalculatorEvent.CloseParen,
                        operators)
                "=" ->
                    Key(label, CalculatorEvent.Evaluate, operators)
                "sin", "cos", "log", "ln", "√" ->
                    Key(
                        label,
                        CalculatorEvent.Function(if (label == "√") "sqrt" else label),
                        functions
                    )
                else -> Key(label, CalculatorEvent.Clear, operators)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bgSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    CalculatorButton(
                        key = key,
                        onEvent = onEvent,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)   // keeps buttons square
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorButton(
    key: Key,
    onEvent: (CalculatorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onEvent(key.event) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = key.bg)
    ) {
        Text(
            text = key.label,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
