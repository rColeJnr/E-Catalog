package com.rick.ui_components.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.rick.data.ui_components.auth.R

@Composable
fun LoginScreen(
    email: String,
    onEmailValueChange: (String) -> Unit,
    isValidEmail: Boolean,
    isEmailInputValid: (String) -> Boolean,
    password: String,
    onPasswordValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisible: () -> Unit,
    showPasswordTips: Boolean,
    showConfirmPassword: Boolean,
    confirmPassword: String,
    onConfirmPasswordValueChange: (String) -> Unit,
    isValidConfirmPassword: Boolean,
    username: String,
    onUsernameValueChange: (String) -> Unit,
    isValidUsername: Boolean,
    showUsernameTips: Boolean,
    screenStateCreate: Boolean,
    onScreenStateCreateValueChange: () -> Unit,
    progressState: Boolean,
    showProgressState: (Boolean) -> Unit,
    onGoogleOneTap: () -> Unit,
    onAuthenticate: (email: String, password: String) -> Unit,
    onCreateAccount: (email: String, password: String, username: String) -> Unit,
    onResetPassword: (String) -> Unit,
    authErrorMsg: String
) {

    val signInText =
        if (screenStateCreate) stringResource(R.string.data_ui_components_auth_create) else stringResource(
            R.string.data_ui_components_auth_sign_in
        )
    val createAccountText =
        if (screenStateCreate) stringResource(R.string.data_ui_components_auth_have_an_account) else stringResource(
            R.string.data_ui_components_auth_create_an_account
        )

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val guidelineFromTop =
        if (screenHeight < 600.dp)
            if (screenStateCreate)
                30.toFloat().div(100) // Create account mode
            else
                34.toFloat().div(100)// Sign in mode
        else
            if (screenStateCreate)
                38.toFloat().div(100) // Create account mode
            else
                40.toFloat().div(100)// Sign in mode

    val buttonsColor = colorResource(id = R.color.data_ui_components_auth_onBackground)

    val density = LocalDensity.current

    val showDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val constraintSet = authConstraintSet(guidelineFromTop)

    ConstraintLayout(
        constraintSet = constraintSet,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.data_ui_components_auth_background))
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painterResource(id = R.drawable.data_ui_components_auth_ic_app_icon),
            contentDescription = null,
            modifier = Modifier
                .layoutId(LayoutRefs.Icon)
                .size(dimensionResource(id = R.dimen.data_ui_components_auth_40dp))
        )

        Text(
            text = stringResource(R.string.data_ui_components_auth_catalogs),
            fontSize = integerResource(id = R.integer.data_ui_components_auth_50).sp,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.layoutId(LayoutRefs.Catalogs)
        )

        Text(
            text = stringResource(R.string.data_ui_components_auth_slogan),
            fontSize = integerResource(id = R.integer.data_ui_components_auth_28).sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.layoutId(LayoutRefs.Slogan)
        )

        val keyboardController = LocalSoftwareKeyboardController.current

        AnimatedVisibilityBox(
            visibility = progressState,
            density = density,
            modifier = Modifier.layoutId(LayoutRefs.ProgressBar)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.data_ui_components_auth_4dp))
                    .size(dimensionResource(R.dimen.data_ui_components_auth_50dp)),
                strokeWidth = 2.dp,
                trackColor = colorResource(id = R.color.data_ui_components_auth_icons),
                strokeCap = StrokeCap.Butt
            )
        }

        AnimatedVisibilityBox(
            visibility = authErrorMsg.isNotBlank(),
            density = density,
            modifier = Modifier
                .width(dimensionResource(id = R.dimen.data_ui_components_auth_282dp))
                .wrapContentHeight()
                .layoutId(LayoutRefs.AuthFailed)
        ) {
            Text(
                text = stringResource(
                    R.string.data_ui_components_auth_failed_to_authenticate,
                    authErrorMsg
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red,
                maxLines = 2
            )
        }
        TextInputRoundRect(
            layoutId = LayoutRefs.Email,
            color = buttonsColor,
            value = email,
            onValueChange = onEmailValueChange,
            label = stringResource(R.string.data_ui_components_auth_email),
            placeholder = stringResource(R.string.data_ui_components_auth_email_placeholder),
            leadingIcon = {
                if (email.isNotEmpty()) {
                    EcsLeadingIcon(visibility = isValidEmail,
                        icon1 = R.drawable.data_ui_components_auth_ic_check,
                        description1 = R.string.data_ui_components_auth_valid_input,
                        icon2 = R.drawable.data_ui_components_auth_ic_x,
                        description2 = R.string.data_ui_components_auth_invalid_input,
                        onClick = { }
                    )
                }
            },
            visualTransformation = false,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Email
            ),
            keyboardActions = KeyboardActions()
        )

        TextInputRoundRect(
            layoutId = LayoutRefs.Password,
            color = buttonsColor,
            value = password,
            onValueChange = onPasswordValueChange,
            label = stringResource(id = R.string.data_ui_components_auth_password),
            leadingIcon = {
                EcsLeadingIcon(visibility = passwordVisible,
                    icon1 = R.drawable.data_ui_components_auth_ic_hide,
                    description1 = R.string.data_ui_components_auth_hide_password,
                    icon2 = R.drawable.data_ui_components_auth_ic_show,
                    description2 = R.string.data_ui_components_auth_show_password,
                    onClick = { onPasswordVisible() })
            },
            placeholder = stringResource(R.string.data_ui_components_auth_password_placeholder),
            visualTransformation = passwordVisible,
            keyboardOptions = KeyboardOptions(
                imeAction = if (screenStateCreate) {
                    ImeAction.Next
                } else {
                    ImeAction.Done
                }, keyboardType = KeyboardType.Password
            ),
            keyboardActions = if (!screenStateCreate) {
                KeyboardActions(onDone = {
                    keyboardController?.hide()
                    onAuthenticate(email, password)
                })
            } else {
                KeyboardActions()
            }
        )

        Spacer(
            modifier = Modifier
                .height(0.dp)
                .layoutId(LayoutRefs.PasswordSpacer)
        )

        AnimatedVisibilityBox(
            visibility = !screenStateCreate,
            density = density,
            modifier = Modifier.layoutId(LayoutRefs.PasswordReset)
        ) {
            Text(text = stringResource(R.string.data_ui_components_auth_forgot_password),
                textAlign = TextAlign.End,
                modifier = Modifier.clickable { showDialog.value = true })
        }

        AnimatedVisibilityBox(
            visibility = screenStateCreate && showPasswordTips,
            density = density,
            modifier = Modifier.layoutId(LayoutRefs.PasswordTips)
        ) {
            BulletListCard {
                BulletList(
                    bullets = listOf(
                        stringResource(R.string.data_ui_components_auth_no_whitespaces),
                        stringResource(R.string.data_ui_components_auth_a_chars_min),
                        stringResource(R.string.data_ui_components_auth_latino_alphabet)
                    )
                )
            }
        }

        AnimatedVisibilityBox(
            visibility = screenStateCreate && showConfirmPassword,
            density = density,
            modifier = Modifier.layoutId(LayoutRefs.ConfirmPassword)
        ) {
            TextInputRoundRect(
                layoutId = LayoutRefs.ConfirmPassword,
                color = buttonsColor,
                value = confirmPassword,
                onValueChange = onConfirmPasswordValueChange,
                label = stringResource(R.string.data_ui_components_auth_confirm_password),
                leadingIcon = {
                    EcsLeadingIcon(visibility = isValidConfirmPassword,
                        icon1 = R.drawable.data_ui_components_auth_ic_check,
                        description1 = R.string.data_ui_components_auth_valid_input,
                        icon2 = R.drawable.data_ui_components_auth_ic_x,
                        description2 = R.string.data_ui_components_auth_invalid_input,
                        onClick = {}
                    )
                },
                placeholder = stringResource(id = R.string.data_ui_components_auth_password_placeholder),
                visualTransformation = passwordVisible,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next, keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions()
            )
        }

        Spacer(
            modifier = Modifier
                .height(0.dp)
                .layoutId(LayoutRefs.UsernameSpacer)
        )

        AnimatedVisibilityBox(
            visibility = screenStateCreate,
            density = density,
            fromTop = dimensionResource(id = R.dimen.data_ui_components_auth_minus_12dp),
            modifier = Modifier.layoutId(LayoutRefs.Username)
        ) {
            TextInputRoundRect(
                layoutId = LayoutRefs.Username,
                color = buttonsColor,
                value = username,
                onValueChange = onUsernameValueChange,
                label = stringResource(id = R.string.data_ui_components_auth_username),
                visualTransformation = false,
                leadingIcon = {
                    EcsLeadingIcon(
                        visibility = isValidUsername,
                        icon1 = R.drawable.data_ui_components_auth_ic_check,
                        description1 = R.string.data_ui_components_auth_valid_input,
                        icon2 = R.drawable.data_ui_components_auth_ic_x,
                        description2 = R.string.data_ui_components_auth_invalid_input,
                        onClick = {}
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )
        }

        AnimatedVisibilityBox(
            visibility = screenStateCreate && showUsernameTips,
            density = density,
            modifier = Modifier.layoutId(LayoutRefs.UsernameTips)
        ) {
            BulletListCard {
                BulletList(
                    bullets = listOf(
                        stringResource(id = R.string.data_ui_components_auth_no_whitespaces),
                        stringResource(
                            R.string.data_ui_components_auth_username_chars
                        )
                    )
                )
            }
        }

        Box(modifier = Modifier
            .layoutId(LayoutRefs.SignIn)
            .size(
                height = dimensionResource(id = R.dimen.data_ui_components_auth_40dp),
                width = dimensionResource(
                    id = R.dimen.data_ui_components_auth_120dp
                )
            )
            .clickable {
                showProgressState(true)
                if (screenStateCreate) {
                    onCreateAccount(
                        email, password, username
                    )
                } else {
                    onAuthenticate(email, password)
                }
            }) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = buttonsColor,
                    cornerRadius = CornerRadius(60f),
                )
            }
            CrossfadeText(
                state = signInText,
                label = stringResource(id = R.string.data_ui_components_auth_sign_in),
                padding = 5.dp
            )
        }

        AnimatedVisibilityBox(
            modifier = Modifier.layoutId(LayoutRefs.IconsRow),
            visibility = !screenStateCreate,
            density = density,
            fromTop = dimensionResource(id = R.dimen.data_ui_components_auth_minus_12dp)
        ) {
            Box(
                Modifier
                    .wrapContentSize()
                    .clickable {
                        showProgressState(true)
                        onGoogleOneTap()
                    }
                    .padding(8.dp)) {
                Image(
                    painterResource(id = R.drawable.data_ui_components_auth_google_icon),
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.data_ui_components_auth_48dp))
                )
            }

        }

        Box(
            modifier = Modifier
                .layoutId(LayoutRefs.CreateAccount)
                .size(
                    height = dimensionResource(id = R.dimen.data_ui_components_auth_70dp),
                    width = dimensionResource(id = R.dimen.data_ui_components_auth_140dp)
                )
                .clickable {
                    showProgressState(false)
                    onScreenStateCreateValueChange()
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
            CrossfadeText(
                state = createAccountText,
                label = stringResource(id = R.string.data_ui_components_auth_create),
                padding = 16.dp
            )
        }

    }

    if (showDialog.value) {
        PasswordResetDialog(onPasswordReset = { onResetPassword(it) },
            isValidEmail = { isEmailInputValid(it) },
            onDismissRequest = {
                showDialog.value = false
            })
    }
}

enum class LayoutRefs() {
    Icon,
    Catalogs,
    Slogan,
    Email,
    Password,
    ConfirmPassword,
    SignIn,
    IconsRow,
    CreateAccount,
    Username,
    PasswordTips,
    UsernameTips,
    PasswordSpacer,
    UsernameSpacer,
    ProgressBar,
    PasswordReset,
    AuthFailed,
}

@Composable
fun authConstraintSet(guidelineFromTop: Float): ConstraintSet {
    //dimensions
    val twentySix = dimensionResource(id = R.dimen.data_ui_components_auth_26dp)
    val thirtySix = dimensionResource(id = R.dimen.data_ui_components_auth_36dp)
    val fourteen = dimensionResource(id = R.dimen.data_ui_components_auth_14dp)

    return ConstraintSet {
        val icon = createRefFor(LayoutRefs.Icon)
        val catalogs = createRefFor(LayoutRefs.Catalogs)
        val slogan = createRefFor(LayoutRefs.Slogan)
        val authFailed = createRefFor(LayoutRefs.AuthFailed)
        val emailTF = createRefFor(LayoutRefs.Email)
        val passwordTF = createRefFor(LayoutRefs.Password)
        val confirmPasswordTF = createRefFor(LayoutRefs.ConfirmPassword)
        val usernameTF = createRefFor(LayoutRefs.Username)
        val signIn = createRefFor(LayoutRefs.SignIn)
        val iconsRow = createRefFor(LayoutRefs.IconsRow)
        val createAccount = createRefFor(LayoutRefs.CreateAccount)
        val passwordTips = createRefFor(LayoutRefs.PasswordTips)
        val passwordSpacer = createRefFor(LayoutRefs.PasswordSpacer)
        val usernameTips = createRefFor(LayoutRefs.UsernameTips)
        val usernameSpacer = createRefFor(LayoutRefs.UsernameSpacer)
        val passwordReset = createRefFor(LayoutRefs.PasswordReset)
        val progressBar = createRefFor(LayoutRefs.ProgressBar)
        val topGuideline = createGuidelineFromTop(guidelineFromTop)

        constrain(icon) {
            top.linkTo(parent.top, margin = twentySix)
            start.linkTo(parent.start, margin = fourteen)
        }
        constrain(catalogs) {
            top.linkTo(icon.top)
            start.linkTo(icon.end, margin = 6.dp)
            bottom.linkTo(icon.bottom)
        }
        constrain(slogan) {
            top.linkTo(catalogs.bottom, margin = thirtySix)
            start.linkTo(catalogs.start, margin = 2.dp)
        }
        constrain(progressBar) {
            bottom.linkTo(topGuideline)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(authFailed) {
            bottom.linkTo(topGuideline)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(emailTF) {
            top.linkTo(topGuideline)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(passwordTF) {
            top.linkTo(emailTF.bottom, margin = 2.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(passwordSpacer) {
            top.linkTo(passwordTF.bottom)
        }
        constrain(passwordReset) {
            top.linkTo(passwordSpacer.bottom, margin = 2.dp)
            end.linkTo(passwordTF.end)
        }
        constrain(passwordTips) {
            top.linkTo(passwordSpacer.bottom, margin = 6.dp)
            start.linkTo(passwordTF.start, margin = 1.dp)
        }
        constrain(confirmPasswordTF) {
            top.linkTo(passwordTips.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(usernameSpacer) {
            top.linkTo(confirmPasswordTF.bottom)
        }
        constrain(usernameTF) {
            top.linkTo(usernameSpacer.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(usernameTips) {
            top.linkTo(usernameTF.bottom, margin = 6.dp)
            start.linkTo(usernameTF.start, margin = 1.dp)
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
}

@Preview
@Composable
fun PreviewLoginContent() {
    LoginScreen(
        email = "",
        password = "",
        username = "",
        onAuthenticate = { _, _ -> },
        onCreateAccount = { _, _, _ -> },
        onGoogleOneTap = {},
        onResetPassword = {},
        onEmailValueChange = {},
        onPasswordValueChange = {},
        onUsernameValueChange = {},
        onScreenStateCreateValueChange = {},
        screenStateCreate = true,
        progressState = true,
        showProgressState = {},
        isValidEmail = true,
        passwordVisible = false,
        onPasswordVisible = { true },
        showConfirmPassword = true,
        showPasswordTips = false,
        isValidUsername = false,
        onConfirmPasswordValueChange = {},
        showUsernameTips = true,
        confirmPassword = "",
        isEmailInputValid = { true },
        isValidConfirmPassword = true,
        authErrorMsg = "Invalid email"
    )

}