package com.csl.shop

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.bumptech.glide.Glide
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class CacheService : IntentService("CacheService"), AnkoLogger {

    companion object {
        val ACTION_CACHE_DONE = "action_cache_done"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //intentService 會自己destroy, 會循序執行,用thread去跑
    //service要自己家stopService
    override fun onHandleIntent(intent: Intent?) {
        info("onHandleIntent")
        val title = intent?.getStringExtra("TITLE")
        val url = "https://m.media-amazon.com/images/M/MV5BNGVjNWI4ZGUtNzE0MS00YTJmLWE0ZDctN2ZiYTk2YmI3NTYyXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_SY1000_CR0,0,674,1000_AL_.jpg"
        info("Downloading $title  $url ")
        Glide.with(this).download(url)

        sendBroadcast(Intent(ACTION_CACHE_DONE))
    }



    override fun onCreate() {
        super.onCreate()

        info { "onCreate()" }
    }

    override fun onDestroy() {
        super.onDestroy()
        info { "onDestroy()" }
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        info ( "onStartCommand()" )
//
//        //service因系統資源短缺 被殺掉後是否要生回來
//        return START_STICKY
//    }
}