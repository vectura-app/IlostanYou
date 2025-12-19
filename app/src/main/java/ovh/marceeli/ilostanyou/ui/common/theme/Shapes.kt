package ovh.marceeli.ilostanyou.ui.common.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
object ExpressiveListItemShapes {
    val topListItemShape =
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    val middleListItemShape = RoundedCornerShape(4.dp)
    val bottomListItemShape =
        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    val singleListItemShape =
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
}