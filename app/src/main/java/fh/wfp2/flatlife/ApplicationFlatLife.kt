package fh.wfp2.flatlife

import android.app.Application
import timber.log.Timber

class ApplicationFlatLife : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.i("onCreate called")
    }

}