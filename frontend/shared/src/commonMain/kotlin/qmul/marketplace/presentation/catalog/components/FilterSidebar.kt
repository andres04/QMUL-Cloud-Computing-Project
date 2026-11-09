package qmul.marketplace.presentation.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qmul.marketplace.domain.model.ProductFilter

/**
 * Filter sidebar component matching the desktop wireframe design.
 */
@Composable
fun FilterSidebar(
    modifier: Modifier = Modifier,
    filter: ProductFilter = ProductFilter(),
    onApplyFilter: (ProductFilter) -> Unit
) {
    var selectedCategory by remember(filter) { mutableStateOf(filter.category ?: "Ceramics") }
    var priceRange by remember(filter) {
        mutableStateOf(
            (filter.minPrice?.toFloat() ?: 20f)..(filter.maxPrice?.toFloat() ?: 180f)
        )
    }
    var selectedRating by remember(filter) { mutableStateOf(filter.rating ?: 4.0) }
    var selectedLocation by remember(filter) { mutableStateOf(filter.location ?: "United States") }

    Column(
        modifier = modifier
            .width(220.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Filters",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Section
        Text(
            text = "Category",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        listOf("Ceramics", "Prints & posters", "Jewellery").forEach { cat ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedCategory = cat }
                    .padding(vertical = 2.dp)
            ) {
                Checkbox(
                    checked = selectedCategory == cat,
                    onCheckedChange = { selectedCategory = cat },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1E293B))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = cat,
                    fontSize = 12.sp,
                    color = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Price Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Price range",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$${priceRange.start.toInt()} – $${priceRange.endInclusive.toInt()}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }

        RangeSlider(
            value = priceRange,
            onValueChange = { priceRange = it },
            valueRange = 0f..300f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF1E293B),
                activeTrackColor = Color(0xFF1E293B),
                inactiveTrackColor = Color(0xFFE2E8F0)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Rating Section
        Text(
            text = "Rating",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { selectedRating = 4.0 }
        ) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Rating Filter Star",
                    tint = if (i <= selectedRating) Color(0xFF0F172A) else Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Ships from Section
        Text(
            text = "Ships from",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        listOf("United States", "Europe", "Asia Pacific").forEach { loc ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedLocation = loc }
                    .padding(vertical = 2.dp)
            ) {
                Checkbox(
                    checked = selectedLocation == loc,
                    onCheckedChange = { selectedLocation = loc },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1E293B))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = loc,
                    fontSize = 12.sp,
                    color = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apply filters Button
        Button(
            onClick = {
                onApplyFilter(
                    ProductFilter(
                        category = selectedCategory,
                        minPrice = priceRange.start.toDouble(),
                        maxPrice = priceRange.endInclusive.toDouble(),
                        rating = selectedRating,
                        location = selectedLocation
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "Apply filters",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
private fun FilterSidebarPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FilterSidebar(onApplyFilter = {})
        }
    }
}
