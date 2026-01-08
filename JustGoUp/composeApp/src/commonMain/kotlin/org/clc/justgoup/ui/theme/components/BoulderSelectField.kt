package org.clc.justgoup.ui.theme.components

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.clc.justgoup.ui.theme.BoulderTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoulderSelectField(
    state: TextFieldState,
    placeholder: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    val colors = BoulderTheme.colors
    val typography = BoulderTheme.typography

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) onExpandedChange(!expanded) },
        modifier = modifier
    ) {
        TextField(
            state = state,
            modifier = Modifier.menuAnchor(
                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                enabled = enabled
            ),
            enabled = enabled,
            readOnly = true,
            isError = isError,
            lineLimits = TextFieldLineLimits.SingleLine,
            placeholder = {
                Text(
                    text = placeholder,
                    style = typography.body,
                    color = colors.textSecondary
                )
            },
            textStyle = typography.body.copy(color = colors.textPrimary),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                disabledContainerColor = colors.surface.copy(alpha = 0.6f),
                errorContainerColor = colors.surface,

                focusedIndicatorColor = if (isError) colors.error else colors.primary,
                unfocusedIndicatorColor = if (isError) colors.error else colors.textSecondary,
                errorIndicatorColor = colors.error,

                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                disabledTextColor = colors.textSecondary,

                cursorColor = colors.primary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(option, style = typography.body)
                    },
                    onClick = {
                        state.setTextAndPlaceCursorAtEnd(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}
