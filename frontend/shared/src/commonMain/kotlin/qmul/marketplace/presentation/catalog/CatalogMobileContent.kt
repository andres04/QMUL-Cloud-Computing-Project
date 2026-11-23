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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qmul.marketplace.domain.model.Product
import qmul.marketplace.navigation.Destination
import qmul.marketplace.presentation.catalog.components.CatalogTopBar
import qmul.marketplace.presentation.catalog.components.CategoryChips
import qmul.marketplace.presentation.catalog.components.FilterSidebar
import qmul.marketplace.presentation.catalog.components.ProductCard

/**
 * Mobile Content layout with native Material 3 icons, search toggle with automatic focus, mobile Sort/Filter bottom sheets, and infinite scroll.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogMobileContent(
    state: CatalogState,
    onEvent: (CatalogEvents) -> Unit,
    onNavigate: (Destination) -> Unit = {},
    currentDestination: Destination = Destination.Catalog,
    modifier: Modifier = Modifier
) {
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    var showSortBottomSheet by remember { mutableStateOf(false) }

    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sortSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val gridState = rememberLazyGridState()
    val focusRequester = remember { FocusRequester() }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= gridState.layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !state.isLoading && state.currentPage < state.totalPages) {
            onEvent(CatalogEvents.OnLoadNextPage)
        }
    }

    LaunchedEffect(currentDestination) {
        if (currentDestination is Destination.Search) {
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Mobile Top Bar with Sort and Filter buttons
        CatalogTopBar(
            searchQuery = state.searchQuery,
            onSearchQueryChanged = { onEvent(CatalogEvents.OnSearch(it)) },
            cartItemCount = state.cartItems.size,
            isMobile = true,
            onMobileSortClick = { showSortBottomSheet = true },
            onMobileFilterClick = { showFilterBottomSheet = true }
        )

        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Search Input shown ONLY when active destination is Search tab with auto-focus
            if (currentDestination is Destination.Search) {
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
                        .padding(bottom = 12.dp)
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF0F172A),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    )
                )
            }

            // Category Chips Row with Custom filter memory support
            CategoryChips(
                selectedCategory = state.selectedCategory,
                hasCustomFilter = state.customFilter != null,
                onCategorySelected = { onEvent(CatalogEvents.OnCategorySelected(it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2-Column Product Grid with Infinite Scroll (No Pagination Bar)
            Box(modifier = Modifier.weight(1f)) {
                if (state.products.isEmpty() && state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0F172A)
                    )
                } else if (state.products.isEmpty()) {
                    Text(
                        text = "No products found",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.products) { product ->
                            ProductCard(
                                product = product,
                                onAddToCart = { onEvent(CatalogEvents.OnAddToCart(it)) },
                                onProductClick = { onNavigate(Destination.ProductDetail(it.id)) }
                            )
                        }

                        if (state.isLoading && state.products.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color(0xFF0F172A), modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet sliding up from bottom
    if (showFilterBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterBottomSheet = false },
            sheetState = filterSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Header with Title + Close / Collapse Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Collapse / Close Button
                    IconButton(
                        onClick = { showFilterBottomSheet = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Collapse Filters",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = Color(0xFFE2E8F0),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Filter Sidebar Form Content
                FilterSidebar(
                    modifier = Modifier.fillMaxWidth(),
                    filter = state.appliedFilter,
                    maxAvailablePrice = state.maxAvailablePrice,
                    onApplyFilter = { newFilter ->
                        showFilterBottomSheet = false
                        onEvent(CatalogEvents.OnFilterApplied(newFilter))
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Sort Options Bottom Sheet sliding up from bottom
    if (showSortBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortBottomSheet = false },
            sheetState = sortSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Header with Title + Close / Collapse Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort products by",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { showSortBottomSheet = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Sort",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = Color(0xFFE2E8F0),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                val sortOptions = listOf(
                    "Most popular",
                    "Price: Low to High",
                    "Price: High to Low",
                    "Newest arrivals",
                    "Customer rating"
                )

                sortOptions.forEach { option ->
                    val isSelected = state.sortBy == option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(CatalogEvents.OnSortByChanged(option))
                                showSortBottomSheet = false
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                onEvent(CatalogEvents.OnSortByChanged(option))
                                showSortBottomSheet = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0F172A))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
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
