package com.rick.ui_components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.rick.data.ui_components.auth.R

enum class LayoutRefs() {
    Icon,
    Catalogs,
    Slogan,
    Email,
    Password,
    SignIn,
    IconsRow,
    CreateAccount,
    Username,
    PasswordTips,
    UsernameTips,
    ProgressBar,
    PasswordReset
}

@Composable
fun LoginScreen(
    email: String,
    password: String,
    userName: String,
    onEmailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onUsernameValueChange: (String) -> Unit,
    onGoogleOneTap: () -> Unit,
    onAuthenticate: (email: String, password: String) -> Unit,
    onCreateAccount: (email: String, password: String, username: String) -> Unit,
    onResetPassword: (String) -> Unit,
    screenState: Boolean,
    onScreenStateValueChange: (Boolean) -> Unit,
    progressState: Boolean,
    showProgressState: (Boolean) -> Unit,
    isValidEmail: (String) -> Boolean
) {

    val signInText = if (screenState) "Create" else "Sign in"
    val createAccountText = if (screenState) "Have an\nAccount?" else "Create an\nAccount"
    val guidelineFromTop: Float by animateFloatAsState(
        if (screenState) 0.38f else 0.42f,
        label = "guideline"
    )
    val buttonsColor = colorResource(id = R.color.data_ui_components_auth_onBackground)

    val density = LocalDensity.current

    val showDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val constraintSet = ConstraintSet {
        val icon = createRefFor(LayoutRefs.Icon)
        val catalogs = createRefFor(LayoutRefs.Catalogs)
        val slogan = createRefFor(LayoutRefs.Slogan)
        val emailTF = createRefFor(LayoutRefs.Email)
        val passwordTF = createRefFor(LayoutRefs.Password)
        val usernameTF = createRefFor(LayoutRefs.Username)
        val signIn = createRefFor(LayoutRefs.SignIn)
        val iconsRow = createRefFor(LayoutRefs.IconsRow)
        val createAccount = createRefFor(LayoutRefs.CreateAccount)
        val passwordTips = createRefFor(LayoutRefs.PasswordTips)
        val usernameTips = createRefFor(LayoutRefs.UsernameTips)
        val passwordReset = createRefFor(LayoutRefs.PasswordReset)
        val progressBar = createRefFor(LayoutRefs.ProgressBar)
        val topGuideline = createGuidelineFromTop(guidelineFromTop)

        constrain(progressBar) {
            top.linkTo(signIn.top)
            bottom.linkTo(signIn.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(icon) {
            top.linkTo(parent.top, margin = 26.dp)
            start.linkTo(parent.start, margin = 14.dp)
        }
        constrain(catalogs) {
            top.linkTo(icon.top)
            start.linkTo(icon.end, margin = 8.dp)
            bottom.linkTo(icon.bottom)
        }
        constrain(slogan) {
            top.linkTo(catalogs.bottom, margin = 36.dp)
            start.linkTo(catalogs.start, margin = 2.dp)
        }
        constrain(emailTF) {
            top.linkTo(topGuideline)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(passwordTF) {
            top.linkTo(emailTF.bottom, margin = 12.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(passwordReset) {
            top.linkTo(passwordTF.bottom, margin = 2.dp)
            end.linkTo(passwordTF.end)
        }
        constrain(passwordTips) {
            top.linkTo(passwordTF.bottom, margin = 2.dp)
            start.linkTo(passwordTF.start, margin = 4.dp)
        }
        constrain(usernameTF) {
            top.linkTo(passwordTips.bottom, margin = 12.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(usernameTips) {
            top.linkTo(usernameTF.bottom, margin = 2.dp)
            start.linkTo(usernameTF.start, margin = 4.dp)
        }
        constrain(signIn) {
            top.linkTo(usernameTips.bottom)
            bottom.linkTo(iconsRow.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(iconsRow) {
            top.linkTo(signIn.bottom)
            bottom.linkTo(createAccount.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(createAccount) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
    }

    ConstraintLayout(
        constraintSet = constraintSet,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.data_ui_components_auth_background))
    ) {
        Image(
            painterResource(id = R.drawable.data_ui_components_auth_ic_app_icon),
            contentDescription = null,
            modifier = Modifier
                .layoutId(LayoutRefs.Icon)
                .size(40.dp)
        )

        Text(
            text = "Catalogs",
            fontSize = 50.sp,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .layoutId(LayoutRefs.Catalogs)
        )

        Text(
            text = "Discover what\nto binge watch\nnext,\n\nor read",
            fontSize = 28.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.layoutId(LayoutRefs.Slogan)
        )

        val keyboardController = LocalSoftwareKeyboardController.current

        TextInputRoundRect(
            layoutId = LayoutRefs.Email,
            color = buttonsColor,
            value = email,
            onValueChange = onEmailValueChange,
            label = "Email",
            placeholder = "Email@user.com",
            visualTransformation = false,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            keyboardActions = KeyboardActions()
        )

        TextInputRoundRect(
            layoutId = LayoutRefs.Password,
            color = buttonsColor,
            value = password,
            onValueChange = onPasswordValueChange,
            label = "Password",
            placeholder = "******",
            visualTransformation = true,
            keyboardOptions = KeyboardOptions(
                imeAction = if (screenState) {
                    ImeAction.Next
                } else {
                    ImeAction.Done
                },
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = if (screenState) {
                KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        onAuthenticate(email, password)
                    }
                )
            } else {
                KeyboardActions()
            }
        )

        AnimatedVisibilityBox(
            screenState = !screenState,
            density = density,
            modifier = Modifier.layoutId(LayoutRefs.PasswordReset)
        ) {
            Text(
                text = "Forgot password?",
                textAlign = TextAlign.End,
                modifier = Modifier.clickable { showDialog.value = true })
        }

        AnimatedVisibilityBox(
            screenState = screenState,
            density = density,
            modifier = Modifier.layoutId(LayoutRefs.PasswordTips)
        ) {
            BulletList(
                title = "Password",
                bullets = listOf(
                    "No whitespaces",
                    "A minimum of 6 characters",
                    "Only letters of the latino alphabet"
                ),
            )
        }

        AnimatedVisibilityBox(
            screenState = screenState,
            density = density,
            fromTop = (-12).dp,
            modifier = Modifier.layoutId(LayoutRefs.Username)
        ) {
            TextInputRoundRect(
                layoutId = LayoutRefs.Username,
                color = buttonsColor,
                value = userName,
                onValueChange = onUsernameValueChange,
                label = "Username",
                visualTransformation = false,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )
        }
        AnimatedVisibilityBox(
            screenState = progressState,
            density = density,
            modifier = Modifier.layoutId(LayoutRefs.ProgressBar)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(4.dp).size(50.dp),
                strokeWidth = 2.dp,
                trackColor = colorResource(id = R.color.data_ui_components_auth_icons),
                strokeCap = StrokeCap.Butt
            )
        }

        AnimatedVisibilityBox(
            screenState = screenState,
            density = density,
            modifier = Modifier.layoutId(LayoutRefs.UsernameTips)
        ) {
            BulletList(
                title = "Username",
                bullets = listOf("No whitespaces", "3 to 12 characters")
            )
        }

        Box(
            modifier = Modifier
                .layoutId(LayoutRefs.SignIn)
                .size(height = 40.dp, width = 120.dp)
                .clickable {
                    showProgressState(true)
                    if (screenState) {
                        onCreateAccount(
                            email,
                            password,
                            userName
                        )
                    } else {
                        onAuthenticate(email, password)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = buttonsColor,
                    cornerRadius = CornerRadius(60f),
                )
            }
            CrossfadeText(state = signInText, label = "sign_in", padding = 5.dp)
        }

        AnimatedVisibilityBox(
            modifier = Modifier.layoutId(LayoutRefs.IconsRow),
            screenState = !screenState,
            density = density,
            fromTop = (-12).dp
        ) {
            Box(
                Modifier
                    .wrapContentSize()
                    .clickable {
                        showProgressState(true)
                        onGoogleOneTap()
                    }
                    .padding(8.dp)
            ) {
                Image(
                    painterResource(id = R.drawable.data_ui_components_auth_google_icon),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }

        }

        Box(
            modifier = Modifier
                .layoutId(LayoutRefs.CreateAccount)
                .size(height = 70.dp, width = 140.dp)
                .clickable {
                    showProgressState(false)
                    onScreenStateValueChange(screenState)
                },
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
            ) {
                drawArc(
                    color = buttonsColor,
                    startAngle = -180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    size = Size(size.width, size.height * 2)
                )
            }
            CrossfadeText(state = createAccountText, label = "create", padding = 16.dp)
        }

    }

    if (showDialog.value) {
        PasswordResetDialog(
            onPasswordReset = { onResetPassword(it) },
            isValidEmail = { isValidEmail(it) },
            onDismissRequest = { showDialog.value = false }
        )
    }
}

@Composable
fun CrossfadeText(state: String, label: String, padding: Dp) {
    Crossfade(targetState = state, label = label) { text ->
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding),
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            color = colorResource(id = R.color.data_ui_components_auth_text),
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun TextInputRoundRect(
    layoutId: LayoutRefs,
    color: Color,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    visualTransformation: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
) {

    Box(modifier = Modifier.layoutId(layoutId)) {
        Canvas(Modifier.size(height = 70.dp, width = 282.dp)) {
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(26f)
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
            modifier = Modifier
                .size(height = 68.dp, width = 280.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp)
        )
    }

}

@Composable
fun BulletList(title: String, bullets: List<String>) {
    val bullet = "\t\u2022\t"
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(text = "$title must contain:")
        Text(
            buildAnnotatedString {
                bullets.forEach {
                    withStyle(style = paragraphStyle) {
                        append(bullet)
                        append(it)
                    }
                }
            }
        )
    }
}

@Composable
fun AnimatedVisibilityBox(
    screenState: Boolean,
    density: Density,
    fromTop: Dp = 0.dp,
    modifier: Modifier,
    composable: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = screenState,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { fromTop.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        composable()
    }
}

@Composable
fun PasswordResetDialog(
    onPasswordReset: (String) -> Unit,
    isValidEmail: (String) -> Boolean,
    onDismissRequest: () -> Unit
) {
    val email = rememberSaveable {
        mutableStateOf("")
    }
    val invalidEmail = rememberSaveable {
        mutableStateOf(false)
    }
    val container = colorResource(id = R.color.data_ui_components_auth_background)
    val content = colorResource(id = R.color.data_ui_components_auth_text)
    val button = colorResource(id = R.color.data_ui_components_auth_onBackground)


    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(205.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = container, contentColor = content)
        ) {
            Column {
                Text(
                    "Request password reset",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp, vertical = 8.dp)
                )
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    placeholder = {
                        Text(
                            text = "user@mail.com"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true
                )
                AnimatedVisibility(visible = invalidEmail.value) {
                    Text(
                        text = "\t\u2022\tInvalid email",
                        color = Color.Red,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismissRequest()
                        }, modifier = Modifier
                            .width(100.dp)
                            .padding(8.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(color = button)
                    ) {
                        Text(
                            text = "" +
                                    "Dismiss", color = content
                        )
                    }
                    TextButton(
                        onClick = {
                            if (isValidEmail(email.value)) {
                                onPasswordReset(email.value)
                                onDismissRequest()
                            } else {
                                invalidEmail.value = true
                            }
                        }, modifier = Modifier
                            .width(100.dp)
                            .padding(8.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(color = button)
                    ) {
                        Text(text = "Send", color = content)
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun PreviewPasswordResetDialog() {
    PasswordResetDialog(
        onPasswordReset = {},
        isValidEmail = { true },
        onDismissRequest = {}
    )
}

@Preview
@Composable
fun PreviewLoginContent() {
    LoginScreen(
        email = "",
        password = "",
        userName = "",
        onAuthenticate = { _, _ -> },
        onCreateAccount = { _, _, _ -> },
        onGoogleOneTap = {},
        onResetPassword = {},
        onEmailValueChange = {},
        onPasswordValueChange = {},
        onUsernameValueChange = {},
        onScreenStateValueChange = {},
        screenState = true,
        progressState = true,
        showProgressState = {},
        isValidEmail = { true }
    )
}