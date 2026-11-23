package qmul.marketplace.presentation.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qmul.marketplace.domain.usecase.AddToCartUseCase
import qmul.marketplace.domain.usecase.GetProductDetailUseCase

class ProductDetailViewModel(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailState())
    val uiState: StateFlow<ProductDetailState> = _uiState.asStateFlow()

    private var currentProductId: String? = null

    fun loadProduct(productId: String) {
        if (currentProductId == productId && _uiState.value.productDetail != null) return
        currentProductId = productId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getProductDetailUseCase(productId)
            result.fold(
                onSuccess = { detail ->
                    _uiState.update {
                        it.copy(
                            productDetail = detail,
                            selectedColor = detail.colors.firstOrNull()?.name ?: "",
                            selectedQuantity = 1,
                            selectedImageIndex = 0,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load product detail"
                        )
                    }
                }
            )
        }
    }

    fun onEvent(event: ProductDetailEvents) {
        when (event) {
            is ProductDetailEvents.LoadProduct -> loadProduct(event.productId)
            is ProductDetailEvents.OnQuantityChanged -> {
                _uiState.update {
                    val newQty = (it.selectedQuantity + event.delta).coerceAtLeast(1)
                    it.copy(selectedQuantity = newQty)
                }
            }
            is ProductDetailEvents.OnColorSelected -> {
                _uiState.update { it.copy(selectedColor = event.colorName) }
            }
            is ProductDetailEvents.OnImageSelected -> {
                _uiState.update { it.copy(selectedImageIndex = event.index) }
            }
            ProductDetailEvents.OnAddToCart -> {
                val state = _uiState.value
                val detail = state.productDetail ?: return
                viewModelScope.launch {
                    repeat(state.selectedQuantity) {
                        addToCartUseCase(detail.toProduct())
                    }
                    _uiState.update {
                        it.copy(userMessage = "Added ${state.selectedQuantity} × '${detail.title}' to basket")
                    }
                }
            }
            ProductDetailEvents.OnBuyNow -> {
                val state = _uiState.value
                val detail = state.productDetail ?: return
                viewModelScope.launch {
                    repeat(state.selectedQuantity) {
                        addToCartUseCase(detail.toProduct())
                    }
                    _uiState.update {
                        it.copy(userMessage = "Proceeding to checkout with ${state.selectedQuantity} × '${detail.title}'")
                    }
                }
            }
            ProductDetailEvents.OnUserMessageConsumed -> {
                _uiState.update { it.copy(userMessage = null) }
            }
        }
    }
}
