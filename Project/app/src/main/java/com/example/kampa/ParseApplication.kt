package com.example.kampa

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.kampa.models.*
import com.parse.Parse
import com.parse.ParseObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class ParseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG)

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See https://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        val builder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.networkInterceptors().add(httpLoggingInterceptor)

        // Define subclasses
        ParseObject.registerSubclass(Denuncia::class.java)
        ParseObject.registerSubclass(Publicacion::class.java)
        ParseObject.registerSubclass(PublicacionTags::class.java)
        ParseObject.registerSubclass(Rol::class.java)
        ParseObject.registerSubclass(Sitio::class.java)
        ParseObject.registerSubclass(SitioTag::class.java)
        ParseObject.registerSubclass(Tag::class.java)
        ParseObject.registerSubclass(TipoSitio::class.java)
        ParseObject.registerSubclass(UsuarioSitio::class.java)
        ParseObject.registerSubclass(UsuarioTag::class.java)
        ParseObject.registerSubclass(Wishlist::class.java)
        ParseObject.registerSubclass(WishlistSitio::class.java)

        // Get secret keys
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)

        val applicationIdValue = ai.metaData["parseApplicationId"]
        val parseApplicationId = applicationIdValue.toString()

        val clientKeyValue = ai.metaData["parseClientKey"]
        val parseClientKey = clientKeyValue.toString()

        // set applicationId, and server server based on the values in the back4app settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId(parseApplicationId) // should correspond to Application ID env variable
            .clientKey(parseClientKey)  // should correspond to Client key env variable" +
            .server("https://parseapi.back4app.com").build())
    }
}