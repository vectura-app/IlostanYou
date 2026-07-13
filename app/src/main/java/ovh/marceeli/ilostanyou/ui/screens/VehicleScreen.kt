package ovh.marceeli.ilostanyou.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Factory
import androidx.compose.material.icons.outlined.RailwayAlert
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.tomaszk8266.ilostan.api.types.toPhotoUrl
import org.koin.androidx.compose.koinViewModel
import ovh.marceeli.ilostanyou.ui.common.components.ExpressiveListItems
import ovh.marceeli.ilostanyou.ui.common.components.ListItemContent
import ovh.marceeli.ilostanyou.ui.viewmodels.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleScreen(
    vehicleId: Int,
    onBack: () -> Unit,
    viewModel: VehicleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(vehicleId) {
        viewModel.loadVehicle(vehicleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    state.vehicle?.let { 
                        Text(it.name, fontWeight = FontWeight.Bold) 
                    } ?: Text("Vehicle Details") 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.vehicle?.photos?.let { photos ->
                    HorizontalCenteredHeroCarousel(
                        state = rememberCarouselState { photos.size },
                        modifier = Modifier.fillMaxWidth()
                            .wrapContentHeight(),
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) { n ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photos[n].id.toPhotoUrl())
                                .crossfade(true)
                                .build(),
                            contentDescription = photos[n].description,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.height(205.dp)
                                .maskClip(MaterialTheme.shapes.extraLarge)
                        )
                    }
                }

                ExpressiveListItems(buildList {
                    state.vehicle?.manufacturingYear?.let { add(ListItemContent(
                        overline = { Text("Manufacturing year") },
                        title = { Text(it.toString()) },
                        leading = { Icon(Icons.Outlined.CalendarMonth, null) }
                    )) }

                    state.vehicle?.statusHistory?.lastOrNull()?.let { add(ListItemContent(
                        overline = { Text("Status") },
                        title = { Text(it.status.name) },
                        leading = { Icon(Icons.Outlined.RailwayAlert, null) }
                    )) }

                    state.vehicle?.ownershipHistory?.lastOrNull()?.let { add(ListItemContent(
                        overline = { Text("Carrier") },
                        title = { Text(it.carrier ?: "Unknown") },
                        subtitle = { Text("Owned by ${it.owner}") },
                        leading = { Icon(Icons.Outlined.Train, null) }
                    )) }

                    state.vehicle?.factoryNumber?.let { add(ListItemContent(
                        overline = { Text("Factory number") },
                        title = { Text(it) },
                        leading = { Icon(Icons.Outlined.Factory, null) }
                    )) }
                })
            }

            if (state.isLoading) LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}