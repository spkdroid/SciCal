package com.dija.scical.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dija.scical.data.repository.CalculatorRepository
import com.dija.scical.domain.model.AngleMode
import com.dija.scical.domain.usecase.EvaluateExpressionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val evaluate: EvaluateExpressionUseCase,
    private val repository: CalculatorRepository
) : ViewModel() {

    data class State(
        val display: String = "",
        val result: String = "",
        val angleMode: AngleMode = AngleMode.DEG
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Digit -> append(event.char.toString())
            is CalculatorEvent.Operator -> append(event.symbol)
            is CalculatorEvent.Function -> append("${event.name}(")
            is CalculatorEvent.OpenParen -> append("(")
            is CalculatorEvent.CloseParen -> append(")")
            CalculatorEvent.Evaluate -> solve()
            CalculatorEvent.Clear -> _state.value = State(angleMode = repository.settings.value.angleMode)
            CalculatorEvent.Backspace -> _state.update { it.copy(display = it.display.dropLast(1)) }
            CalculatorEvent.ToggleAngle -> {
                repository.toggleAngle()
                _state.update { it.copy(angleMode = repository.settings.value.angleMode) }
            }
            is CalculatorEvent.Memory -> handleMemory(event)
        }
    }

    private fun append(text: String) {
        _state.update { it.copy(display = it.display + text) }
    }

    private fun solve() = viewModelScope.launch {
        evaluate(_state.value.display, repository.memory.value)
            .onSuccess { value ->
                _state.update { state ->
                    state.copy(result = value.toPlainString())
                }
            }
            .onFailure {
                _state.update { state -> state.copy(result = "ERR") }
            }
    }

    private fun handleMemory(m: CalculatorEvent.Memory) {
        val current = repository.memory.value
        val res = _state.value.result.toBigDecimalOrNull() ?: return
        when (m) {
            CalculatorEvent.Memory.Add -> repository.memoryAdd(res)
            CalculatorEvent.Memory.Clear -> repository.memoryClear()
            CalculatorEvent.Memory.Store -> repository.memoryStore(res)
            CalculatorEvent.Memory.Recall -> append(current.toPlainString())
        }
    }
}
