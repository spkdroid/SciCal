package com.dija.scical.domain.usecase

import com.dija.scical.data.repository.CalculatorRepository
import com.dija.scical.util.ExpressionEvaluator
import java.math.BigDecimal
import javax.inject.Inject

class EvaluateExpressionUseCase @Inject constructor(
    private val repository: CalculatorRepository
) {
    operator fun invoke(input: String, memory: BigDecimal): Result<BigDecimal> = runCatching {
        ExpressionEvaluator.evaluate(
            input = input,
            angleMode = repository.settings.value.angleMode,
            memory = memory,
            precision = repository.settings.value.precision
        )
    }
}
