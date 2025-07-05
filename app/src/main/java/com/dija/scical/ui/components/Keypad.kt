package com.dija.scical.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dija.scical.ui.screen.CalculatorEvent

@Composable
fun Keypad(onEvent: (CalculatorEvent) -> Unit) {
    val keys = listOf(
        listOf("7", "8", "9", "+"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "*"),
        listOf("0", ".", "=", "/"),
        listOf("(", ")", "sin", "cos"),
        listOf("log", "ln", "√", "^")
    )
    Column {
        keys.forEach { row ->
            Row {
                row.forEach { key ->
                    Button(
                        onClick = {
                            onEvent(
                                when (key) {
                                    in "0".."9", "." -> CalculatorEvent.Digit(key[0])
                                    "+", "-", "*", "/", "^" -> CalculatorEvent.Operator(key)
                                    "(", ")" -> if (key == "(")
                                        CalculatorEvent.OpenParen else CalculatorEvent.CloseParen
                                    "=" -> CalculatorEvent.Evaluate
                                    "sin", "cos", "log", "ln", "√" ->
                                        CalculatorEvent.Function(if (key == "√") "sqrt" else key)
                                    else -> CalculatorEvent.Clear
                                }
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(key)
                    }
                }
            }
        }
    }
}