package com.ibs.spato.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.ibs.spato.activities.LoginActivity
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginService : Service() {
    @Inject
    lateinit var session: Session

    override fun onCreate() {
        super.onCreate()
        ApplicationComponentObject.create(this).injectLoginService(this)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        GlobalScope.launch(Dispatchers.Main) {
            delay(60 * 1000)
            val token = session.getString(Constants.LOGIN_TOKEN)
            val activeInactive = session.getString(Constants.LOGIN_SCREEN_ACTIVE_OR_NOT_KEY)
            if (token == Constants.NO_DATA && activeInactive != Constants.ACTIVE) {
                val i = Intent(this@LoginService, LoginActivity::class.java)
                i.putExtra(Constants.KEY, Constants.VALUE)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }
        return START_NOT_STICKY
    }
}