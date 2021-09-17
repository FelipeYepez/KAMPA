package com.example.kampa

import android.app.Application
import com.example.kampa.Models.Sitios
import com.parse.Parse
import com.parse.ParseObject




class ParseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Register your parse models
        //ParseObject.registerSubclass(Comentar.class)
        ParseObject.registerSubclass(Sitios::class.java)

        // set applicationId, and server server based on the values in the back4app settings.
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("um1OJkETCX6MCGQzYOhBaBdVsmlQF89bNzOqqH8b")
                .clientKey("tCcNXJ2DH3nKsFNesQ3fXLo2qLB5x2xHdUyFlsIA")
                .server("https://parseapi.back4app.com")
                .build()
        )
    }
}