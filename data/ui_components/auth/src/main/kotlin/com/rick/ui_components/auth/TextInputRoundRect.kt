package com.rick.ui_components.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.rick.data.ui_components.auth.R


@Composable
fun TextInputRoundRect(
    layoutId: LayoutRefs,
    color: Color,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable () -> Unit = {},
    placeholder: String = "",
    visualTransformation: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
) {

    Box(modifier = Modifier.layoutId(layoutId)) {
        Canvas(
            Modifier
                .size(
                    height = dimensionResource(id = R.dimen.data_ui_components_auth_68dp),
                    width = dimensionResource(id = R.dimen.data_ui_components_auth_282dp)
                )
                .padding(top = 6.dp)
        ) {
            drawRoundRect(
                color = color, cornerRadius = CornerRadius(26f)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            label = { Text(text = label) },
            placeholder = { Text(text = placeholder) },
            visualTransformation = if (visualTransformation) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            trailingIcon = leadingIcon,
            modifier = Modifier
                .size(
                    height = dimensionResource(id = R.dimen.data_ui_components_auth_68dp),
                    width = dimensionResource(id = R.dimen.data_ui_components_auth_280dp)
                )
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp)
        )
    }

}