package org.clc.justgoup.ui.theme.components

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun BoulderTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
    maxLength: Int? = null,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    singleLine: Boolean = false,
    showClearButton: Boolean = false
) {
    val colors = BoulderTheme.colors
    val typography = BoulderTheme.typography

    TextField(
        value = value,
        onValueChange = { onValueChange(it.take(maxLength ?: it.length)) },
        modifier = modifier,
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        placeholder = { Text(placeholder, style = typography.body, color = colors.textSecondary) },
        textStyle = typography.body.copy(color = colors.textPrimary),
        isError = isError,
        trailingIcon = if (showClearButton && value.isNotEmpty() && enabled) {
            {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = colors.textSecondary
                    )
                }
            }
        } else null,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            disabledContainerColor = colors.surface.copy(alpha = 0.6f),
            errorContainerColor = colors.surface, // <-- add this

            focusedIndicatorColor = if (isError) colors.error else colors.primary,
            unfocusedIndicatorColor = if (isError) colors.error else colors.textSecondary,
            errorIndicatorColor = colors.error,

            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            disabledTextColor = colors.textSecondary,

            cursorColor = if (isError) colors.error else colors.primary
        )
    )
}
