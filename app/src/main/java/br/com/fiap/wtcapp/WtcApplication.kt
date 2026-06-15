package br.com.fiap.wtcapp

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

/** Application entry point. [@HiltAndroidApp] bootstraps the Hilt dependency graph. */
@HiltAndroidApp
class WtcApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Only collect crash reports from release builds so debug noise stays out of the dashboard.
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
    }
}
