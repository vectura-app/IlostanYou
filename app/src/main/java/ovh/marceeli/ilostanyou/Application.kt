package ovh.marceeli.ilostanyou

import android.app.Application
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import ovh.marceeli.ilostanyou.di.viewModelModule

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            androidLogger()
            modules(viewModelModule)
        }

        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}