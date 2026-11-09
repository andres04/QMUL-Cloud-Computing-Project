package qmul.marketplace.presentation.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pagination controls matching the wireframe design: < 1 2 3 4 5 6 >
 */
@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous Button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                .clickable(enabled = currentPage > 1) { onPageChanged(currentPage - 1) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "<", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 14.sp)
        }

        // Page Number Buttons
        val pagesToShow = (1..totalPages.coerceAtLeast(6)).toList()
        pagesToShow.forEach { page ->
            val isSelected = page == currentPage
            val backgroundColor = if (isSelected) Color(0xFF1E293B) else Color.White
            val textColor = if (isSelected) Color.White else Color(0xFF1E293B)

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(backgroundColor)
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                    .clickable { onPageChanged(page) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.toString(),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor,
                    fontSize = 13.sp
                )
            }
        }

        // Next Button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                .clickable(enabled = currentPage < totalPages) { onPageChanged(currentPage + 1) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = ">", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 14.sp)
        }
    }
}

@Preview
@Composable
private fun PaginationControlsPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PaginationControls(
                currentPage = 1,
                totalPages = 6,
                onPageChanged = {}
            )
        }
    }
}
