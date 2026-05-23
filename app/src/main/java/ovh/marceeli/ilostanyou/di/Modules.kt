package ovh.marceeli.ilostanyou.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ovh.marceeli.ilostanyou.ui.viewmodels.DashboardViewModel
import ovh.marceeli.ilostanyou.ui.viewmodels.VehicleViewModel

val viewModelModule = module {
    viewModel { DashboardViewModel() }
    viewModel { VehicleViewModel() }
}