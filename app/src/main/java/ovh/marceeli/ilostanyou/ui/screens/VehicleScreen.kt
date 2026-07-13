package ovh.marceeli.ilostanyou.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.tomaszk8266.ilostan.api.types.toPhotoUrl
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.koin.androidx.compose.koinViewModel
import ovh.marceeli.ilostanyou.ui.common.components.ExpressiveListItems
import ovh.marceeli.ilostanyou.ui.common.components.ListItemContent
import ovh.marceeli.ilostanyou.ui.viewmodels.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class, FormatStringsInDatetimeFormats::class)
@Composable
fun VehicleScreen(
    vehicleId: Int,
    onBack: () -> Unit,
    viewModel: VehicleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val ownersSheetState = rememberModalBottomSheetState()
    var showOwnersBottomSheet by remember { mutableStateOf(false) }

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
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val currentCarrierAndOwner = state.vehicle?.ownershipHistory
                    ?.filter { it.transferDate != null }
                    ?.maxByOrNull { it.transferDate!! }
                    ?: state.vehicle?.ownershipHistory?.lastOrNull()

                state.vehicle?.photos?.let { photos ->
                    HorizontalCenteredHeroCarousel(
                        state = rememberCarouselState { photos.size },
                        modifier = Modifier
                            .fillMaxWidth()
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
                            modifier = Modifier
                                .height(205.dp)
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

                    currentCarrierAndOwner?.let { add(ListItemContent.fromStrings(
                            overline = "Carrier",
                            title = it.carrier ?: "Unknown",
                            subtitle = "Owned by ${it.owner}",
                            leading = { Icon(Icons.Outlined.Train, null) },
                            onClick = { showOwnersBottomSheet = true }
                        )) }

                    state.vehicle?.factoryNumber?.let { add(ListItemContent.fromStrings(
                        overline = "Factory number",
                        title = it,
                        leading = { Icon(Icons.Outlined.Factory, null) }
                    )) }
                })

                state.vehicle?.eventHistory?.ifEmpty { null }?.let { events ->
                    Text(
                        text = "Event history",
                        fontSize = 20.sp
                    )

                    ExpressiveListItems(events.map { event ->
                        ListItemContent.fromStrings(
                            overline = event.date.format(
                                LocalDate.Format { byUnicodePattern("dd.MM.yyyy") }
                            ),
                            title = event.description
                        )
                    })
                }

                state.vehicle?.repairHistory?.ifEmpty { null }?.let { repairs ->
                    Text(
                        text = "Repair history",
                        fontSize = 20.sp
                    )

                    ExpressiveListItems(repairs.map { repair ->
                        ListItemContent.fromStrings(
                            overline = repair.finishDate.format(
                                LocalDate.Format { byUnicodePattern("dd.MM.yyyy") }
                            ),
                            title = repair.type,
                            subtitle = repair.zntk
                        )
                    })
                }

                state.vehicle?.statusHistory?.ifEmpty { null }?.let { statuses ->
                    Text(
                        text = "Status history",
                        fontSize = 20.sp
                    )

                    ExpressiveListItems(statuses.map { status ->
                        ListItemContent.fromStrings(
                            overline = status.date.format(
                                LocalDate.Format { byUnicodePattern("dd.MM.yyyy") }
                            ),
                            title = status.status.name,
                            subtitle = status.comment
                        )
                    })
                }
            }

            if (state.isLoading) LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )

            state.vehicle?.ownershipHistory?.let { ownershipHistory ->
                if (showOwnersBottomSheet) ModalBottomSheet(
                    onDismissRequest = { showOwnersBottomSheet = false },
                    sheetState = ownersSheetState
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Carriers and ownership history",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 25.sp
                        )

                        ExpressiveListItems(ownershipHistory.map { ownershipEntry ->
                            ListItemContent.fromStrings(
                                overline = ownershipEntry.transferDate?.format(
                                    LocalDate.Format { byUnicodePattern("dd.MM.yyyy") }
                                ),
                                title = ownershipEntry.carrier ?: ownershipEntry.owner,
                                subtitle = if (ownershipEntry.carrier != null)
                                    "Owned by ${ownershipEntry.owner}" else null
                            )
                        })
                    }
                }
            }
        }
    }
}