package ovh.marceeli.ilostanyou.di

import org.koin.dsl.module
import ovh.marceeli.ilostanyou.ui.viewmodels.SharedDashboardViewModel

val viewModelModule = module {
    single { SharedDashboardViewModel() }
}