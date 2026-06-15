package br.com.fiap.wtcapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Application entry point. [@HiltAndroidApp] bootstraps the Hilt dependency graph. */
@HiltAndroidApp
class WtcApplication : Application()
