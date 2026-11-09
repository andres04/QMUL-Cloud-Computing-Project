package qmul.marketplace.presentation.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val defaultCategories = listOf(
    "All",
    "Ceramics",
    "Prints",
    "Jewellery",
    "Vintage",
    "Home & living",
    "Art"
)

/**
 * Category chips component allowing category selection.
 */
@Composable
fun CategoryChips(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    categories: List<String> = defaultCategories,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = if (category == "All") selectedCategory.isNullOrBlank() else selectedCategory == category
            val backgroundColor = if (isSelected) Color(0xFF1E293B) else Color.White
            val textColor = if (isSelected) Color.White else Color(0xFF1E293B)
            val borderColor = Color(0xFF1E293B)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .clickable {
                        onCategorySelected(if (category == "All") null else category)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Preview
@Composable
private fun CategoryChipsPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CategoryChips(
                selectedCategory = "Ceramics",
                onCategorySelected = {}
            )
        }
    }
}
