package ovh.marceeli.ilostanyou.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ovh.marceeli.ilostanyou.ui.common.theme.ExpressiveListItemShapes

@Composable
fun ExpressiveListItems(
    items: List<ListItemContent>,
    colors: ListItemColors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
) {
    ExpressiveListItemsIndexed(items) { _, item, shape ->
        ExpressiveListItem(shape, colors, item)
    }
}

@Composable
inline fun <T> ExpressiveListItems(
    items: List<T>,
    crossinline content: @Composable (item: T, shape: RoundedCornerShape) -> Unit
) {
    ExpressiveListItemsIndexed(items) { _, item, shape ->
        content(item, shape)
    }
}

@Composable
inline fun <T> ExpressiveListItemsIndexed(
    items: List<T>,
    crossinline content: @Composable (index: Int, item: T, shape: RoundedCornerShape) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEachIndexed { index, item ->
            val shape = when {
                items.size == 1 -> ExpressiveListItemShapes.singleListItemShape
                index == 0 -> ExpressiveListItemShapes.topListItemShape
                index == items.lastIndex -> ExpressiveListItemShapes.bottomListItemShape
                else -> ExpressiveListItemShapes.middleListItemShape
            }
            content(index, item, shape)
        }
    }
}

@Composable
fun ExpressiveLazyListItems(
    items: List<ListItemContent>,
    colors: ListItemColors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
) {
    ExpressiveLazyListItemsIndexed(items) { _, item, shape ->
        ExpressiveListItem(shape, colors, item)
    }
}

@Composable
inline fun <T> ExpressiveLazyListItems(
    items: List<T>,
    crossinline content: @Composable (item: T, shape: RoundedCornerShape) -> Unit
) {
    ExpressiveListItemsIndexed(items) { _, item, shape ->
        content(item, shape)
    }
}

@Composable
inline fun <T> ExpressiveLazyListItemsIndexed(
    items: List<T>,
    crossinline content: @Composable (index: Int, item: T, shape: RoundedCornerShape) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(items) { index, item ->
            val shape = when {
                items.size == 1 -> ExpressiveListItemShapes.singleListItemShape
                index == 0 -> ExpressiveListItemShapes.topListItemShape
                index == items.lastIndex -> ExpressiveListItemShapes.bottomListItemShape
                else -> ExpressiveListItemShapes.middleListItemShape
            }
            content(index, item, shape)
        }
    }
}

@Composable
fun ExpressiveListItem(
    shape: RoundedCornerShape,
    colors: ListItemColors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    content: ListItemContent
) {
    ListItem(
        colors = colors,
        leadingContent = content.leading,
        headlineContent = content.title,
        supportingContent = content.subtitle,
        overlineContent = content.overline,
        modifier = Modifier
            .clip(shape)
            .clickable(
                enabled = content.onClick != null,
                onClick = content.onClick ?: { }
            )
    )
}

data class ListItemContent(
    val title: @Composable () -> Unit,
    val subtitle: @Composable (() -> Unit)? = null,
    val overline: @Composable (() -> Unit)? = null,
    val leading: @Composable (() -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
) {
    companion object {
        fun fromStrings(
            title: String,
            subtitle: String? = null,
            overline: String? = null,
            leading: @Composable (() -> Unit)? = null,
            onClick: (() -> Unit)? = null
        ) = ListItemContent(
            title = { Text(title) },
            subtitle = subtitle?.let { { Text(it) } },
            overline = overline?.let { { Text(it) } },
            leading = leading,
            onClick = onClick
        )
    }
}