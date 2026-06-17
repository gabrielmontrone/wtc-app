package br.com.fiap.wtcapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * Application entry point. [@HiltAndroidApp] bootstraps the Hilt dependency graph.
 *
 * Also provides Coil's [ImageLoader] backed by the app's authenticated [OkHttpClient], so
 * chat photos served by the backend (`/api/v1/attachments/{id}`, behind JWT) load correctly.
 */
@HiltAndroidApp
class WtcApplication : Application(), ImageLoaderFactory {
    @Inject
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        // Only collect crash reports from release builds so debug noise stays out of the dashboard.
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .build()
}
