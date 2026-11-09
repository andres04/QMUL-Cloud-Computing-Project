package qmul.marketplace.presentation.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Desktop layout for the Sign Up screen matching the desktop wireframe (Top bar with Logo and 'Back' button, centered Sign Up form).
 */
@Composable
fun SignUpDesktopContent(
    modifier: Modifier = Modifier,
    state: SignUpState,
    onEvent: (SignUpEvents) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Desktop Top Bar with Logo on left and 'Back' button on right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "QMUL Marketplace",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 'Back' Button on Top-Right
            Button(
                onClick = { onEvent(SignUpEvents.OnBackClicked) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Text(text = "Back", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        // Centered Form Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.width(420.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Sign Up",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Error Message
                state.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // 1. Username
                SignUpField(
                    label = "Username",
                    value = state.username,
                    onValueChange = { onEvent(SignUpEvents.OnUsernameChanged(it)) },
                    placeholder = "Enter your username"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. First Name
                SignUpField(
                    label = "First Name",
                    value = state.firstName,
                    onValueChange = { onEvent(SignUpEvents.OnFirstNameChanged(it)) },
                    placeholder = "Enter your first name"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Last Name
                SignUpField(
                    label = "Last Name",
                    value = state.lastName,
                    onValueChange = { onEvent(SignUpEvents.OnLastNameChanged(it)) },
                    placeholder = "Enter your last name"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Email
                SignUpField(
                    label = "Email",
                    value = state.email,
                    onValueChange = { onEvent(SignUpEvents.OnEmailChanged(it)) },
                    placeholder = "Enter your email"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Password
                SignUpField(
                    label = "Password",
                    value = state.password,
                    onValueChange = { onEvent(SignUpEvents.OnPasswordChanged(it)) },
                    placeholder = "Enter your password",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Re-enter Password
                SignUpField(
                    label = "Re-enter Password",
                    value = state.confirmPassword,
                    onValueChange = { onEvent(SignUpEvents.OnConfirmPasswordChanged(it)) },
                    placeholder = "Re-enter your password",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Continue Button
                Button(
                    onClick = { onEvent(SignUpEvents.OnContinueClicked) },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
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
}

@Preview
@Composable
private fun SignUpDesktopContentPreview() {
    MaterialTheme {
        SignUpDesktopContent(
            state = SignUpState(),
            onEvent = {}
        )
    }
}
