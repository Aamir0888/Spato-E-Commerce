package com.ibs.spato.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ibs.spato.R
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.service.LoginService
import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApplicationComponentObject.create(this).injectSplashScreen(this)
        if (session.getString(Constants.LANG_SETTING_KEY) == Constants.GERMAN){
            Utils.setAppLocale(this, "de")
        } else{
            Utils.setAppLocale(this, "")
        }
        setContentView(R.layout.activity_splash)

        GlobalScope.launch(Dispatchers.Main) {
            delay(Constants.SPLASH_SCREEN_TIME)
            startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
            finish()
        }
    }
}