package ovh.marceeli.ilostanyou.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.koin.androidx.compose.koinViewModel
import ovh.marceeli.ilostanyou.ui.common.components.ExpressiveLazyListItems
import ovh.marceeli.ilostanyou.ui.common.components.ListItemContent
import ovh.marceeli.ilostanyou.ui.viewmodels.DashboardState
import ovh.marceeli.ilostanyou.ui.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DashboardScreen(
    onVehicleSelect: (Int) -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchBarExpanded by remember { mutableStateOf(false) }
    val searchTextFieldState = rememberTextFieldState()

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val topPaddingAnimatable = remember { Animatable(0f) }
    var topPadding by remember { mutableStateOf(0.dp) }
    var isInitial by remember { mutableStateOf(true) }

    LaunchedEffect(searchTextFieldState.text) {
        viewModel.onQueryChange(searchTextFieldState.text as String)
    }

    LaunchedEffect(searchBarExpanded, topPadding) {
        val target = if (searchBarExpanded)
            0.dp else maxOf(0.dp, topPadding - statusBarPadding)

        if (isInitial && topPadding > 0.dp) {
            topPaddingAnimatable.snapTo(target.value)
            isInitial = false
        } else topPaddingAnimatable.animateTo(
            targetValue = target.value,
            animationSpec = tween(durationMillis = 300)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = "Ilostan You",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Medium)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { innerPadding ->
            LaunchedEffect(innerPadding) {
                topPadding = innerPadding.calculateTopPadding()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(top = SearchBarDefaults.InputFieldHeight + 12.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Feed",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    ExpressiveLazyListItems(state.feedItems.map { item ->
                        ListItemContent(
                            title = { Text(item.title) },
                            subtitle = item.subtitle?.let { { Text(it) } },
                            leading = item.photoUrl?.let { url -> {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(url)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = item.subtitle,
                                    modifier = Modifier.width(56.dp),
                                    contentScale = ContentScale.FillWidth,
                                )
                            } }
                        )
                    })

                    Spacer(Modifier.height(16.dp))
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(24.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background,
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }

        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = topPaddingAnimatable.value.dp)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    state = searchTextFieldState,
                    onSearch = { },
                    expanded = searchBarExpanded,
                    onExpandedChange = { searchBarExpanded = it },
                    placeholder = { Text("Search") }
                )
            },
            expanded = searchBarExpanded,
            onExpandedChange = { searchBarExpanded = it },
        ) {
            Box {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.categories) { category ->
                            FilterChip(
                                selected = state.selectedCategoryIds.contains(category.id),
                                onClick = { viewModel.toggleCategory(category.id) },
                                label = { Text(category.name) }
                            )
                        }
                    }

                    ExpressiveLazyListItems(
                        items = state.suggestions.map { suggestion ->
                            ListItemContent(
                                title = { Text(suggestion.name) },
                                subtitle = suggestion.description?.let { { Text(it) } },
                                leading = suggestion.photoUrl?.let { url -> {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(url)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = suggestion.name,
                                        modifier = Modifier.width(56.dp),
                                        contentScale = ContentScale.FillWidth,
                                    )
                                } },
                                onClick = {
                                    when (suggestion.type) {
                                        DashboardState.Suggestion.Type.Vehicle -> onVehicleSelect(suggestion.id)
                                        DashboardState.Suggestion.Type.Series -> searchTextFieldState.edit {
                                            replace(
                                                start = 0,
                                                end = length,
                                                // Only use dash in Polish and Polish-like series
                                                // naming conventions which consists of both letters
                                                // and digits, Czech, German and many other only
                                                // contain digits and space is used as a separator
                                                text = suggestion.rawName!! + if (suggestion.rawName.toIntOrNull() == null) "-" else " "
                                            )
                                        }
                                    }
                                }
                            )
                        },
                        colors = ListItemDefaults.colors()
                    )
                }

                if (state.searchLoading) LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )
            }
        }
    }
}