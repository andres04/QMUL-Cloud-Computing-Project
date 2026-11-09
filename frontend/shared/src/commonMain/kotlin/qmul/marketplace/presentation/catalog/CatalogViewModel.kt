package qmul.marketplace.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qmul.marketplace.domain.model.Product
import qmul.marketplace.domain.usecase.GetProductsUseCase

class CatalogViewModel(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogState())
    val uiState: StateFlow<CatalogState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun onEvent(event: CatalogEvents) {
        when (event) {
            is CatalogEvents.OnSearch -> {
                _uiState.update { it.copy(searchQuery = event.query, currentPage = 1) }
                loadProducts()
            }
            is CatalogEvents.OnFilterApplied -> {
                _uiState.update { it.copy(appliedFilter = event.filter, currentPage = 1) }
                loadProducts()
            }
            is CatalogEvents.OnCategorySelected -> {
                _uiState.update { currentState ->
                    val newFilter = currentState.appliedFilter.copy(category = event.category)
                    currentState.copy(selectedCategory = event.category, appliedFilter = newFilter, currentPage = 1)
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
            is CatalogEvents.OnSortByChanged -> {
                _uiState.update { it.copy(sortBy = event.sortBy, currentPage = 1) }
                loadProducts()
            }
            is CatalogEvents.OnDismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
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
                page = state.currentPage
            )

            result.fold(
                onSuccess = { pageResult ->
                    _uiState.update {
                        it.copy(
                            products = pageResult.products,
                            currentPage = pageResult.currentPage,
                            totalPages = pageResult.totalPages,
                            totalItems = pageResult.totalItems,
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

    private fun addToCart(product: Product) {
        _uiState.update { currentState ->
            currentState.copy(cartItems = currentState.cartItems + product)
        }
    }
}
