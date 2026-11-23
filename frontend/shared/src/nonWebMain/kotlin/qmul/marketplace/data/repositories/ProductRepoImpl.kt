package qmul.marketplace.data.repositories

import qmul.marketplace.data.db.ProductDao
import qmul.marketplace.data.db.ProductEntity
import qmul.marketplace.data.network.ProductApi
import qmul.marketplace.domain.model.Product
import qmul.marketplace.domain.model.ProductFilter
import qmul.marketplace.domain.model.ProductPage
import qmul.marketplace.domain.repository.ProductRepository

private val sampleImages = listOf(
    "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=600&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=600&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=600&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1615865417491-9941019fbc00?w=600&auto=format&fit=crop"
)

class ProductRepoImpl(
    private val productApi: ProductApi,
    private val productDao: ProductDao
) : ProductRepository {

    // Mock dataset with 48 items to demonstrate pagination, sorting, and Coil 3 image loading
    private val mockProducts = listOf(
        Product("1", "Speckled Stoneware Mug", 28.00, 5.0, sampleImages[0], "Ceramics"),
        Product("2", "Linen Throw Blanket", 74.00, 4.0, sampleImages[1], "Home & living"),
        Product("3", "Riso Print — Coastal Fog", 42.00, 5.0, sampleImages[2], "Prints"),
        Product("4", "Brass Hoop Earrings", 36.00, 4.0, sampleImages[3], "Jewellery"),
        Product("5", "Walnut Serving Board", 58.00, 5.0, sampleImages[4], "Ceramics"),
        Product("6", "Hand-dyed Indigo Scarf", 49.00, 4.0, sampleImages[1], "Vintage"),
        Product("7", "Ceramic Planter — Large", 64.00, 5.0, sampleImages[0], "Ceramics"),
        Product("8", "Vintage Levi's 501 — W32", 88.00, 4.0, sampleImages[4], "Vintage"),
        Product("9", "Terracotta Espresso Cup", 22.00, 5.0, sampleImages[0], "Ceramics"),
        Product("10", "Woven Wool Tapestry", 115.00, 4.5, sampleImages[1], "Home & living"),
        Product("11", "Botanical Art Print", 34.00, 4.0, sampleImages[2], "Prints"),
        Product("12", "Silver Minimalist Ring", 45.00, 5.0, sampleImages[3], "Jewellery"),
        Product("13", "Handmade Clay Pitcher", 62.00, 4.5, sampleImages[0], "Ceramics"),
        Product("14", "Vintage Leather Tote", 140.00, 5.0, sampleImages[4], "Vintage"),
        Product("15", "Minimalist Table Lamp", 95.00, 4.0, sampleImages[1], "Home & living"),
        Product("16", "Abstract Geometric Print", 50.00, 4.5, sampleImages[2], "Art"),
        Product("17", "Glazed Porcelain Bowl", 38.00, 5.0, sampleImages[0], "Ceramics"),
        Product("18", "Cotton Fringe Cushion", 44.00, 4.0, sampleImages[1], "Home & living"),
        Product("19", "Screenprint — City Lights", 65.00, 4.5, sampleImages[2], "Prints"),
        Product("20", "Gold Plated Pendant", 78.00, 5.0, sampleImages[3], "Jewellery"),
        Product("21", "Rustic Teak Coasters", 25.00, 4.0, sampleImages[4], "Home & living"),
        Product("22", "Vintage Brass Candleholders", 55.00, 4.5, sampleImages[4], "Vintage"),
        Product("23", "Stoneware Salad Bowl", 48.00, 5.0, sampleImages[0], "Ceramics"),
        Product("24", "Retro Vinyl Record Frame", 30.00, 4.0, sampleImages[2], "Art"),
        Product("25", "Handwoven Macrame Hanger", 26.00, 4.5, sampleImages[1], "Home & living"),
        Product("26", "Linocut Print — Mountain Range", 40.00, 5.0, sampleImages[2], "Prints"),
        Product("27", "Beaded Choker Necklace", 32.00, 4.0, sampleImages[3], "Jewellery"),
        Product("28", "Earthenware Teapot", 70.00, 4.5, sampleImages[0], "Ceramics"),
        Product("29", "Vintage Wool Sweater", 85.00, 5.0, sampleImages[4], "Vintage"),
        Product("30", "Brass Table Clock", 68.00, 4.0, sampleImages[1], "Home & living"),
        Product("31", "Oil Painting — Ocean Breeze", 180.00, 5.0, sampleImages[2], "Art"),
        Product("32", "Ceramic Votive Candleholder", 18.00, 4.0, sampleImages[0], "Ceramics"),
        Product("33", "Embroidered Table Runner", 52.00, 4.5, sampleImages[1], "Home & living"),
        Product("34", "Typography Poster — Create", 28.00, 4.0, sampleImages[2], "Prints"),
        Product("35", "Raw Gemstone Studs", 42.00, 5.0, sampleImages[3], "Jewellery"),
        Product("36", "Vintage Ceramic Jug", 56.00, 4.5, sampleImages[0], "Vintage"),
        Product("37", "Speckled Dinner Plate Set", 92.00, 5.0, sampleImages[0], "Ceramics"),
        Product("38", "Knitted Throw Pillow", 36.00, 4.0, sampleImages[1], "Home & living"),
        Product("39", "Watercolor Print — Autumn", 45.00, 4.5, sampleImages[2], "Art"),
        Product("40", "Hammered Copper Cuff", 58.00, 5.0, sampleImages[3], "Jewellery"),
        Product("41", "Handmade Soap Dish", 16.00, 4.0, sampleImages[0], "Ceramics"),
        Product("42", "Vintage Leather Belt", 38.00, 4.5, sampleImages[4], "Vintage"),
        Product("43", "Linen Napkin Set", 32.00, 4.0, sampleImages[1], "Home & living"),
        Product("44", "Serigraph — Sunrise", 75.00, 5.0, sampleImages[2], "Prints"),
        Product("45", "Pearl Drop Earrings", 64.00, 4.5, sampleImages[3], "Jewellery"),
        Product("46", "Ceramic Diffuser Vase", 48.00, 4.0, sampleImages[0], "Ceramics"),
        Product("47", "Vintage Denim Jacket", 110.00, 5.0, sampleImages[4], "Vintage"),
        Product("48", "Canvas Wall Hanging", 80.00, 4.5, sampleImages[2], "Art")
    )

    override suspend fun getProducts(
        query: String?,
        filter: ProductFilter?,
        sortBy: String?,
        page: Int,
        pageSize: Int
    ): Result<ProductPage> {
        return runCatching {
            // TODO: Connect to REST API using productApi.fetchProducts(...) instead of returning mock dataset
            val filteredProducts = mockProducts.filter { product ->
                val matchesQuery = query.isNullOrBlank() || product.title.contains(query, ignoreCase = true)
                val matchesCategory = when {
                    filter?.categories?.isNotEmpty() == true -> {
                        filter.categories.any { cat ->
                            product.category.equals(cat, ignoreCase = true) ||
                            (cat == "Prints & posters" && product.category == "Prints")
                        }
                    }
                    !filter?.category.isNullOrBlank() -> {
                        product.category.equals(filter.category, ignoreCase = true) ||
                        (filter.category == "Prints & posters" && product.category == "Prints")
                    }
                    else -> true
                }
                val matchesMinPrice = filter?.minPrice == null || product.price >= filter.minPrice
                val matchesMaxPrice = filter?.maxPrice == null || product.price <= filter.maxPrice
                val matchesRating = filter?.rating == null || product.rating >= filter.rating
                matchesQuery && matchesCategory && matchesMinPrice && matchesMaxPrice && matchesRating
            }

            val sortedProducts = when (sortBy) {
                "Highest rated" -> filteredProducts.sortedByDescending { it.rating }
                "Price: Low to High" -> filteredProducts.sortedBy { it.price }
                "Price: High to Low" -> filteredProducts.sortedByDescending { it.price }
                "Newest arrivals" -> filteredProducts.sortedByDescending { it.id.toIntOrNull() ?: 0 }
                else -> filteredProducts // "Most popular" or default
            }

            // Save mock products to local database cache
            productDao.insertProducts(sortedProducts.map { ProductEntity.fromDomain(it) })

            val effectivePageSize = if (pageSize <= 0) 8 else pageSize
            val totalItems = sortedProducts.size
            val totalPages = (totalItems + effectivePageSize - 1) / effectivePageSize
            val safePage = page.coerceIn(1, maxOf(1, totalPages))
            val fromIndex = (safePage - 1) * effectivePageSize
            val toIndex = minOf(fromIndex + effectivePageSize, totalItems)
            val paginatedProducts = if (fromIndex < totalItems) {
                sortedProducts.subList(fromIndex, toIndex)
            } else {
                emptyList()
            }

            val maxPriceInData = mockProducts.maxOfOrNull { it.price } ?: 300.0

            ProductPage(
                products = paginatedProducts,
                currentPage = safePage,
                totalPages = maxOf(1, totalPages),
                totalItems = totalItems,
                maxAvailablePrice = maxPriceInData
            )
        }
    }

    override suspend fun getProductById(id: String): Result<qmul.marketplace.domain.model.ProductDetail> {
        return runCatching {
            val product = mockProducts.find { it.id == id } ?: mockProducts.first()
            val originalPrice = (product.price * 1.2).let { (it * 100).toInt() / 100.0 }

            qmul.marketplace.domain.model.ProductDetail(
                id = product.id,
                title = if (product.id == "1") "Aria 2 Wireless Headphones" else product.title,
                brand = "Northwind Audio",
                price = if (product.id == "1") 249.00 else product.price,
                originalPrice = if (product.id == "1") 299.00 else originalPrice,
                discountPercentage = 17,
                rating = if (product.id == "1") 4.6 else product.rating,
                reviewCount = 1284,
                description = "Adaptive noise cancelling, 38 hours of playback and studio-tuned 40 mm drivers — in a 254 g frame you can wear from the first call to the last.",
                colors = listOf(
                    qmul.marketplace.domain.model.ProductColor("Midnight Black", "#0F172A"),
                    qmul.marketplace.domain.model.ProductColor("Chalk White", "#F1F5F9"),
                    qmul.marketplace.domain.model.ProductColor("Teal Blue", "#0E7490"),
                    qmul.marketplace.domain.model.ProductColor("Deep Purple", "#6B21A8")
                ),
                images = listOf(product.imageUrl) + sampleImages.filter { it != product.imageUrl },
                category = product.category,
                stockStatus = "In stock — ships today",
                deliveryInfo = "Free 2-day, arrives Thu, Mar 12",
                returnPolicy = "30-day, no questions asked",
                warranty = "2-year limited",
                financing = "4 × $62.25 interest-free"
            )
        }
    }
}
