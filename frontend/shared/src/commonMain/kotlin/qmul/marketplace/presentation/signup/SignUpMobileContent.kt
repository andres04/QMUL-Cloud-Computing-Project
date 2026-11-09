package qmul.marketplace.presentation.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Mobile layout for the Sign Up screen matching the wireframe (Header with Back Arrow, 6 fields, and Continue button pinned at bottom).
 */
@Composable
fun SignUpMobileContent(
    modifier: Modifier = Modifier,
    state: SignUpState,
    onEvent: (SignUpEvents) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Top Header Row with Back Arrow + "Sign Up" title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF0F172A),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onEvent(SignUpEvents.OnBackClicked) }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Sign Up",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )
        }

        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        // Middle Scrollable Form Fields Content Area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Error Message
            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Field 1: Username
            SignUpField(
                label = "Username",
                value = state.username,
                onValueChange = { onEvent(SignUpEvents.OnUsernameChanged(it)) },
                placeholder = "Enter your username"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 2: First Name
            SignUpField(
                label = "First Name",
                value = state.firstName,
                onValueChange = { onEvent(SignUpEvents.OnFirstNameChanged(it)) },
                placeholder = "Enter your first name"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 3: Last Name
            SignUpField(
                label = "Last Name",
                value = state.lastName,
                onValueChange = { onEvent(SignUpEvents.OnLastNameChanged(it)) },
                placeholder = "Enter your last name"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 4: Email
            SignUpField(
                label = "Email",
                value = state.email,
                onValueChange = { onEvent(SignUpEvents.OnEmailChanged(it)) },
                placeholder = "Enter your email"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 5: Password
            SignUpField(
                label = "Password",
                value = state.password,
                onValueChange = { onEvent(SignUpEvents.OnPasswordChanged(it)) },
                placeholder = "Enter your password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 6: Re-enter Password
            SignUpField(
                label = "Re-enter Password",
                value = state.confirmPassword,
                onValueChange = { onEvent(SignUpEvents.OnConfirmPasswordChanged(it)) },
                placeholder = "Re-enter your password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Fixed Bottom Container for Continue Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { onEvent(SignUpEvents.OnContinueClicked) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun SignUpField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp, color = Color(0xFF94A3B8)) },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF0F172A),
                unfocusedBorderColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Preview
@Composable
private fun SignUpMobileContentPreview() {
    MaterialTheme {
        SignUpMobileContent(
            state = SignUpState(),
            onEvent = {}
        )
    }
}
