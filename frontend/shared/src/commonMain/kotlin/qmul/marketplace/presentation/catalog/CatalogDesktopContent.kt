package qmul.marketplace.presentation.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qmul.marketplace.domain.model.Product
import qmul.marketplace.navigation.Destination
import qmul.marketplace.presentation.catalog.components.CatalogTopBar
import qmul.marketplace.presentation.catalog.components.CategoryChips
import qmul.marketplace.presentation.catalog.components.FilterSidebar
import qmul.marketplace.presentation.catalog.components.PaginationControls
import qmul.marketplace.presentation.catalog.components.ProductCard

/**
 * Desktop / Wide Screen content layout matching the desktop wireframe.
 */
@Composable
fun CatalogDesktopContent(
    state: CatalogState,
    onEvent: (CatalogEvents) -> Unit,
    onNavigate: (Destination) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Navigation Bar
        CatalogTopBar(
            searchQuery = state.searchQuery,
            onSearchQueryChanged = { onEvent(CatalogEvents.OnSearch(it)) },
            cartItemCount = state.cartItems.size,
            isMobile = false,
            onLoginClick = { onNavigate(Destination.Login) },
            onBasketClick = { onNavigate(Destination.Basket) }
        )

        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 20.dp)
        ) {
            // Category Chips Row
            CategoryChips(
                selectedCategory = state.selectedCategory,
                onCategorySelected = { onEvent(CatalogEvents.OnCategorySelected(it)) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Results Header Stats & Sort Dropdown Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (state.totalItems > 0) state.totalItems else "1,284"} results · Handmade & vintage",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.weight(1f))

                // Sort By Selector Box
                Column {
                    Text(
                        text = "Sort by",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${state.sortBy}  ▼",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Desktop Content: Sidebar + Grid
            Row(
                modifier = Modifier.weight(1f)
            ) {
                // Left Filter Sidebar
                FilterSidebar(
                    filter = state.appliedFilter,
                    onApplyFilter = { onEvent(CatalogEvents.OnFilterApplied(it)) }
                )

                Spacer(modifier = Modifier.width(24.dp))

                // Right Product Grid
                Box(
                    modifier = Modifier.weight(1f)
                ) {
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
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Pagination
            PaginationControls(
                currentPage = state.currentPage,
                totalPages = state.totalPages,
                onPageChanged = { onEvent(CatalogEvents.OnPageChanged(it)) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
private fun CatalogDesktopContentPreview() {
    val sampleProducts = listOf(
        Product("1", "Speckled Stoneware Mug", 28.00, 5.0, "", "Ceramics"),
        Product("2", "Linen Throw Blanket", 74.00, 4.0, "", "Home & living"),
        Product("3", "Riso Print — Coastal Fog", 42.00, 5.0, "", "Prints"),
        Product("4", "Brass Hoop Earrings", 36.00, 4.0, "", "Jewellery"),
        Product("5", "Walnut Serving Board", 58.00, 5.0, "", "Ceramics"),
        Product("6", "Hand-dyed Indigo Scarf", 49.00, 4.0, "", "Vintage"),
        Product("7", "Ceramic Planter — Large", 64.00, 5.0, "", "Ceramics"),
        Product("8", "Vintage Levi's 501 — W32", 88.00, 4.0, "", "Vintage")
    )
    MaterialTheme {
        CatalogDesktopContent(
            state = CatalogState(products = sampleProducts, totalItems = 1284, totalPages = 6),
            onEvent = {}
        )
    }
}
