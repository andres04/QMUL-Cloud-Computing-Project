package qmul.marketplace.presentation.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qmul.marketplace.domain.model.Product
import qmul.marketplace.navigation.Destination
import qmul.marketplace.presentation.catalog.components.CatalogTopBar
import qmul.marketplace.presentation.catalog.components.CategoryChips
import qmul.marketplace.presentation.catalog.components.ProductCard

/**
 * Mobile Content layout with native Material 3 icons, search toggle, and infinite scroll (no pagination).
 */
@Composable
fun CatalogMobileContent(
    state: CatalogState,
    onEvent: (CatalogEvents) -> Unit,
    onNavigate: (Destination) -> Unit = {},
    currentDestination: Destination = Destination.Catalog,
    modifier: Modifier = Modifier
) {
    var showSearchInput by remember { mutableStateOf(currentDestination is Destination.Search) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Mobile Top Bar (Clean header with status bars padding)
        CatalogTopBar(
            searchQuery = state.searchQuery,
            onSearchQueryChanged = { onEvent(CatalogEvents.OnSearch(it)) },
            cartItemCount = state.cartItems.size,
            isMobile = true
        )

        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Search Input shown when Search mode/tab is active
            if (showSearchInput || currentDestination is Destination.Search) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onEvent(CatalogEvents.OnSearch(it)) },
                    placeholder = {
                        Text(
                            text = "Search ceramics, prints, vintage denim...",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF0F172A),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    )
                )
            }

            // Category Chips Row
            CategoryChips(
                selectedCategory = state.selectedCategory,
                onCategorySelected = { onEvent(CatalogEvents.OnCategorySelected(it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2-Column Product Grid with Infinite Scroll (No Pagination Bar)
            Box(modifier = Modifier.weight(1f)) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0F172A)
                    )
                } else if (state.products.isEmpty()) {
                    Text(
                        text = "No products found",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.products) { product ->
                            ProductCard(
                                product = product,
                                onAddToCart = { onEvent(CatalogEvents.OnAddToCart(it)) }
                            )
                        }
                    }
                }
            }
        }

        // Mobile Bottom Navigation Bar using Native Material 3 Icons
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
                icon = if (currentDestination is Destination.Catalog && !showSearchInput) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Home",
                isSelected = currentDestination is Destination.Catalog && !showSearchInput,
                onClick = {
                    showSearchInput = false
                    onNavigate(Destination.Catalog)
                }
            )
            MobileNavItem(
                icon = if (currentDestination is Destination.Search || showSearchInput) Icons.Filled.Search else Icons.Outlined.Search,
                label = "Search",
                isSelected = currentDestination is Destination.Search || showSearchInput,
                onClick = {
                    showSearchInput = true
                    onNavigate(Destination.Search)
                }
            )
            MobileNavItem(
                icon = if (currentDestination is Destination.Basket) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                label = "Basket",
                isSelected = currentDestination is Destination.Basket,
                onClick = {
                    showSearchInput = false
                    onNavigate(Destination.Basket)
                }
            )
            MobileNavItem(
                icon = if (currentDestination is Destination.Login) Icons.Filled.Person else Icons.Outlined.Person,
                label = "Log In",
                isSelected = currentDestination is Destination.Login,
                onClick = {
                    showSearchInput = false
                    onNavigate(Destination.Login)
                }
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
private fun CatalogMobileContentPreview() {
    val sampleProducts = listOf(
        Product("1", "Speckled Stoneware...", 28.00, 5.0, "", "Ceramics"),
        Product("2", "Speckled Stoneware...", 28.00, 5.0, "", "Ceramics")
    )
    MaterialTheme {
        CatalogMobileContent(
            state = CatalogState(products = sampleProducts),
            onEvent = {}
        )
    }
}
