package qmul.marketplace.presentation.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SortIconVector: ImageVector
    get() = ImageVector.Builder(
        name = "SortIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF0F172A))) {
            moveTo(3f, 18f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(-2f)
            lineTo(3f, 16f)
            verticalLineToRelative(2f)
            close()
            moveTo(3f, 6f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(18f)
            lineTo(21f, 6f)
            lineTo(3f, 6f)
            close()
            moveTo(3f, 13f)
            horizontalLineToRelative(12f)
            verticalLineToRelative(-2f)
            lineTo(3f, 11f)
            verticalLineToRelative(2f)
            close()
        }
    }.build()

private val FilterIconVector: ImageVector
    get() = ImageVector.Builder(
        name = "FilterIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF0F172A))) {
            moveTo(10f, 18f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-4f)
            verticalLineToRelative(2f)
            close()
            moveTo(3f, 6f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(18f)
            lineTo(21f, 6f)
            lineTo(3f, 6f)
            close()
            moveTo(6f, 13f)
            horizontalLineToRelative(12f)
            verticalLineToRelative(-2f)
            lineTo(6f, 11f)
            verticalLineToRelative(2f)
            close()
        }
    }.build()

/**
 * Top Navigation Bar for Web and Mobile with statusBarsPadding on mobile for system inset alignment.
 */
@Composable
fun CatalogTopBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    cartItemCount: Int,
    isMobile: Boolean,
    showSearch: Boolean = true,
    showLoginButton: Boolean = true,
    onLoginClick: () -> Unit = {},
    onBasketClick: () -> Unit = {},
    onMobileSortClick: (() -> Unit)? = null,
    onMobileFilterClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .then(if (isMobile) Modifier.statusBarsPadding() else Modifier)
            .padding(horizontal = if (isMobile) 16.dp else 24.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Logo Icon
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

            // App Name
            Text(
                text = "QMUL Marketplace",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isMobile) {
                if (onMobileSortClick != null) {
                    IconButton(
                        onClick = onMobileSortClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = SortIconVector,
                            contentDescription = "Sort",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                if (onMobileFilterClick != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onMobileFilterClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = FilterIconVector,
                            contentDescription = "Filter",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            } else {
                // Desktop Basket Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onBasketClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Basket",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (cartItemCount > 0) "Basket ($cartItemCount)" else "Basket",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A),
                        fontSize = 14.sp
                    )
                }

                if (showLoginButton) {
                    Spacer(modifier = Modifier.width(16.dp))

                    // Desktop Log in Button
                    Button(
                        onClick = onLoginClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(text = "Log in", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (!isMobile && showSearch) {
            Spacer(modifier = Modifier.height(12.dp))

            // Desktop Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = "Search ceramics, prints, vintage denim...",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                modifier = Modifier.fillMaxWidth(0.45f),
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0F172A),
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}

@Preview
@Composable
private fun CatalogTopBarPreview() {
    MaterialTheme {
        CatalogTopBar(
            searchQuery = "",
            onSearchQueryChanged = {},
            cartItemCount = 2,
            isMobile = true,
            onMobileSortClick = {},
            onMobileFilterClick = {}
        )
    }
}
