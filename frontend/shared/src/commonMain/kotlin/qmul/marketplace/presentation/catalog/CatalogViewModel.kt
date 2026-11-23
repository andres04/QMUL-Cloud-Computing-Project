package qmul.marketplace.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qmul.marketplace.domain.model.Product
import qmul.marketplace.domain.model.ProductFilter
import qmul.marketplace.domain.usecase.AddToCartUseCase
import qmul.marketplace.domain.usecase.GetCartUseCase
import qmul.marketplace.domain.usecase.GetProductsUseCase

class CatalogViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogState())
    val uiState: StateFlow<CatalogState> = _uiState.asStateFlow()

    init {
        loadProducts()
        observeCart()
    }

    fun onEvent(event: CatalogEvents) {
        when (event) {
            is CatalogEvents.OnSearch -> {
                _uiState.update {
                    it.copy(
                        searchQuery = event.query,
                        savedSearchQuery = event.query,
                        currentPage = 1
                    )
                }
                loadProducts()
            }
            is CatalogEvents.OnTabChanged -> {
                _uiState.update { currentState ->
                    if (event.isSearchTab) {
                        currentState.copy(
                            searchQuery = currentState.savedSearchQuery,
                            currentPage = 1
                        )
                    } else {
                        val activeSearch = if (currentState.searchQuery.isNotBlank()) {
                            currentState.searchQuery
                        } else {
                            currentState.savedSearchQuery
                        }
                        currentState.copy(
                            savedSearchQuery = activeSearch,
                            searchQuery = "",
                            currentPage = 1
                        )
                    }
                }
                loadProducts()
            }
            is CatalogEvents.OnFilterApplied -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        customFilter = event.filter,
                        selectedCategory = "Custom",
                        appliedFilter = event.filter,
                        currentPage = 1
                    )
                }
                loadProducts()
            }
            is CatalogEvents.OnCategorySelected -> {
                _uiState.update { currentState ->
                    if (event.category == "Custom") {
                        val activeFilter = currentState.customFilter ?: ProductFilter()
                        currentState.copy(
                            selectedCategory = "Custom",
                            appliedFilter = activeFilter,
                            currentPage = 1
                        )
                    } else {
                        val newFilter = ProductFilter(category = event.category)
                        currentState.copy(
                            selectedCategory = event.category,
                            appliedFilter = newFilter,
                            currentPage = 1
                        )
                    }
                }
                loadProducts()
            }
            is CatalogEvents.OnAddToCart -> {
                addToCart(event.product)
            }
            is CatalogEvents.OnPageChanged -> {
                _uiState.update { it.copy(currentPage = event.page) }
                loadProducts()
            }
            CatalogEvents.OnLoadNextPage -> {
                loadNextPageMobile()
            }
            is CatalogEvents.OnSortByChanged -> {
                _uiState.update { it.copy(sortBy = event.sortBy, currentPage = 1) }
                loadProducts()
            }
            is CatalogEvents.OnDismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
            CatalogEvents.OnUserMessageConsumed -> {
                _uiState.update { it.copy(userMessage = null) }
            }
            CatalogEvents.OnRefresh -> {
                loadProducts()
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value

            val result = getProductsUseCase(
                query = state.searchQuery.ifBlank { null },
                filter = state.appliedFilter,
                sortBy = state.sortBy,
                page = state.currentPage,
                pageSize = 8
            )

            result.fold(
                onSuccess = { pageResult ->
                    _uiState.update {
                        it.copy(
                            products = pageResult.products,
                            currentPage = pageResult.currentPage,
                            totalPages = pageResult.totalPages,
                            totalItems = pageResult.totalItems,
                            maxAvailablePrice = pageResult.maxAvailablePrice,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load catalog products"
                        )
                    }
                }
            )
        }
    }

    private fun loadNextPageMobile() {
        val state = _uiState.value
        if (state.isLoading || state.currentPage >= state.totalPages) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val nextPage = state.currentPage + 1

            val result = getProductsUseCase(
                query = state.searchQuery.ifBlank { null },
                filter = state.appliedFilter,
                sortBy = state.sortBy,
                page = nextPage,
                pageSize = 8
            )

            result.fold(
                onSuccess = { pageResult ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            products = currentState.products + pageResult.products,
                            currentPage = pageResult.currentPage,
                            totalPages = pageResult.totalPages,
                            totalItems = pageResult.totalItems,
                            maxAvailablePrice = pageResult.maxAvailablePrice,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            getCartUseCase().collectLatest { items ->
                val flatProducts = items.flatMap { item -> List(item.quantity) { item.product } }
                _uiState.update { it.copy(cartItems = flatProducts) }
            }
        }
    }

    private fun addToCart(product: Product) {
        viewModelScope.launch {
            addToCartUseCase(product)
            _uiState.update {
                it.copy(userMessage = "Added '${product.title}' to basket")
            }
        }
    }
}
