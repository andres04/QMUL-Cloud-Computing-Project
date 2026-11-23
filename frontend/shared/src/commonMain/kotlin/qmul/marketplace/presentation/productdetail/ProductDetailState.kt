package qmul.marketplace.presentation.productdetail

import qmul.marketplace.domain.model.ProductDetail

data class ProductDetailState(
    val productDetail: ProductDetail? = null,
    val selectedColor: String = "",
    val selectedQuantity: Int = 1,
    val selectedImageIndex: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userMessage: String? = null
)
