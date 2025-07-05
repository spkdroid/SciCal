package com.dija.scical.data.repository.impl

import com.dija.scical.data.repository.CalculatorRepository
import com.dija.scical.domain.model.AngleMode
import com.dija.scical.domain.model.CalcSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

class InMemoryCalculatorRepository : CalculatorRepository {
    private val _settings = MutableStateFlow(CalcSettings())
    private val _memory = MutableStateFlow(BigDecimal.ZERO)

    override val settings = _settings.asStateFlow()
    override val memory = _memory.asStateFlow()

    override fun toggleAngle() {
        _settings.update {
            it.copy(angleMode = if (it.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG)
        }
    }

    override fun setPrecision(p: Int) {
        _settings.update { it.copy(precision = p) }
    }

    override fun memoryClear() = _memory.update { BigDecimal.ZERO }

    override fun memoryStore(value: BigDecimal) = _memory.update { value }

    override fun memoryAdd(value: BigDecimal) = _memory.update { it + value }
}