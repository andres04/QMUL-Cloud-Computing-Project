package qmul.marketplace.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qmul.marketplace.navigation.Destination

/**
 * Mobile layout for the Login screen matching Material 3 and brand guidelines.
 */
@Composable
fun LoginMobileContent(
    modifier: Modifier = Modifier,
    state: LoginState,
    onEvent: (LoginEvents) -> Unit,
    onNavigate: (Destination) -> Unit,
    currentDestination: Destination = Destination.Login
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
    ) {
        // Content Area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Icon Logo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "QMUL Marketplace Logo",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF0F172A)
            )

            Text(
                text = "Sign in to QMUL Marketplace",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Error Message Card
            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Username Input Field
            OutlinedTextField(
                value = state.username,
                onValueChange = { onEvent(LoginEvents.OnUsernameChanged(it)) },
                label = { Text("Username", fontSize = 13.sp) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0F172A),
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Input Field
            OutlinedTextField(
                value = state.password,
                onValueChange = { onEvent(LoginEvents.OnPasswordChanged(it)) },
                label = { Text("Password", fontSize = 13.sp) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0F172A),
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login Button
            Button(
                onClick = { onEvent(LoginEvents.OnLoginClicked) },
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
                    Text("Log In", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", fontSize = 13.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign Up",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.clickable { onEvent(LoginEvents.OnSignUpClicked) }
                )
            }
        }

        // Mobile Bottom Navigation Bar
        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MobileNavItem(
                icon = if (currentDestination is Destination.Catalog) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Home",
                isSelected = currentDestination is Destination.Catalog,
                onClick = { onNavigate(Destination.Catalog) }
            )
            MobileNavItem(
                icon = if (currentDestination is Destination.Search) Icons.Filled.Search else Icons.Outlined.Search,
                label = "Search",
                isSelected = currentDestination is Destination.Search,
                onClick = { onNavigate(Destination.Search) }
            )
            MobileNavItem(
                icon = if (currentDestination is Destination.Basket) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                label = "Basket",
                isSelected = currentDestination is Destination.Basket,
                onClick = { onNavigate(Destination.Basket) }
            )
            MobileNavItem(
                icon = if (currentDestination is Destination.Login) Icons.Filled.Person else Icons.Outlined.Person,
                label = "Log In",
                isSelected = currentDestination is Destination.Login,
                onClick = { onNavigate(Destination.Login) }
            )
        }
    }
}

@Composable
private fun MobileNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8)
        )
    }
}

@Preview
@Composable
private fun LoginMobileContentPreview() {
    MaterialTheme {
        LoginMobileContent(
            state = LoginState(),
            onEvent = {},
            onNavigate = {}
        )
    }
}
