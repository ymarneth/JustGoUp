package org.clc.justgoup.ui.theme.components

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun BoulderTextField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
    maxLength: Int? = null
) {
    val colors = BoulderTheme.colors
    val typography = BoulderTheme.typography

    TextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        lineLimits = TextFieldLineLimits.SingleLine,
        placeholder = {
            Text(
                text = placeholder,
                style = typography.body,
                color = colors.textSecondary
            )
        },
        textStyle = typography.body.copy(color = colors.textPrimary),
        inputTransformation = maxLength?.let {
            InputTransformation.maxLength(it)
        },
        isError = isError,
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
