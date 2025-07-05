package com.dija.scical.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dija.scical.ui.components.CalculatorDisplay
import com.dija.scical.ui.components.Keypad

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Angle Mode: ${state.angleMode}", modifier = Modifier
            .clickable { viewModel.onEvent(CalculatorEvent.ToggleAngle) }
            .padding(8.dp)
        )
        CalculatorDisplay(
            expression = state.display,
            result = state.result,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Keypad(viewModel::onEvent)
    }
}

