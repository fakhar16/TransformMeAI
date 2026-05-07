package com.fuza.transformmeai.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    expandWidth: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier =
            modifier
                .then(if (expandWidth) Modifier.fillMaxWidth() else Modifier)
                .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}