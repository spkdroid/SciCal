package com.dija.scical.domain.model

data class CalcSettings(
    val angleMode: AngleMode = AngleMode.DEG,
    val precision: Int = 10
)