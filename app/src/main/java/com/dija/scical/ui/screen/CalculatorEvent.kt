package com.dija.scical.ui.screen

sealed interface CalculatorEvent {
    data class Digit(val char: Char): CalculatorEvent
    data class Operator(val symbol: String): CalculatorEvent
    data class Function(val name: String): CalculatorEvent
    object OpenParen : CalculatorEvent
    object CloseParen : CalculatorEvent
    object Evaluate : CalculatorEvent
    object Clear : CalculatorEvent
    object Backspace : CalculatorEvent
    object ToggleAngle : CalculatorEvent
    sealed interface Memory : CalculatorEvent {
        object Store : Memory
        object Recall : Memory
        object Clear : Memory
        object Add : Memory
    }
}
