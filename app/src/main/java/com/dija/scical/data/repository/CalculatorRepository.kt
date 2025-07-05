package com.dija.scical.data.repository

import com.dija.scical.domain.model.CalcSettings
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal

interface CalculatorRepository {
    val settings: StateFlow<CalcSettings>
    val memory: StateFlow<BigDecimal>

    fun toggleAngle()
    fun setPrecision(p: Int)
    fun memoryClear()
    fun memoryStore(value: BigDecimal)
    fun memoryAdd(value: BigDecimal)
}