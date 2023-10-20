package com.originsdigital.onrewind.sample

import android.app.Application
import com.origins.onrewind.OnRewind
import com.origins.onrewind.domain.Environment

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        OnRewind.initialize(
            OnRewind.InitParams.Builder()
                .setApplicationContext(this)
                .setEnvironment(Environment.Development)
                .build()
        )
    }
}