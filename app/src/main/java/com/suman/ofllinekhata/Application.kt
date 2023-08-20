package com.suman.ofllinekhata

import android.app.Application
import cat.ereza.customactivityoncrash.config.CaocConfig


class App : Application() {
    override fun onCreate() {
        super.onCreate()

       /* CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
            .enabled(false) //default: true
            .showErrorDetails(false) //default: true
            .showRestartButton(false) //default: true
            .logErrorOnRestart(false) //default: true
            .trackActivities(true) //default: false
            .minTimeBetweenCrashesMs(2000) //default: 3000
            .errorDrawable(R.drawable.ic_custom_drawable) //default: bug image
            .restartActivity(YourCustomActivity::class.java) //default: null (your app's launch activity)
            .errorActivity(YourCustomErrorActivity::class.java) //default: null (default error activity)
            .eventListener(YourCustomEventListener()) //default: null
            .customCrashDataCollector(YourCustomCrashDataCollector()) //default: null
            .apply()*/

        //If you use Firebase Crashlytics or ACRA, please initialize them here as explained above.
    }
}