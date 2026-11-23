package qmul.marketplace.presentation.catalog.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import qmul.marketplace.domain.model.Product

/**
 * Product card component rendering image via Coil 3, hover shadow effect, title underline, standard Material ripple click, and add-to-cart action.
 */
@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        onClick = { onProductClick(product) },
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isHovered) 8.dp else 1.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color(0x290F172A)
            )
            .border(
                width = if (isHovered) 1.5.dp else 1.dp,
                color = if (isHovered) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(8.dp)
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 6.dp else 0.dp,
            hoveredElevation = 8.dp,
            pressedElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Image (Coil 3 AsyncImage with fallback to wireframe cross)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
            ) {
                if (product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.5f
                        )
                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = Offset(size.width, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 1.5f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product Title (Underlined when hovered)
            Text(
                text = product.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F172A),
                textDecoration = if (isHovered) TextDecoration.Underline else TextDecoration.None,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            Text(
                text = "$${product.price}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Native Material Star Rating Icons
            val fullStars = product.rating.toInt().coerceIn(0, 5)
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= fullStars) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Star Rating",
                        tint = if (i <= fullStars) Color(0xFF0F172A) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add to Cart Button
            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Add to cart",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProductCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProductCard(
                product = Product(
                    id = "1",
                    title = "Speckled Stoneware Mug",
                    price = 28.00,
                    rating = 5.0,
                    imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop",
                    category = "Ceramics"
                ),
                onAddToCart = {}
            )
        }
    }
}
