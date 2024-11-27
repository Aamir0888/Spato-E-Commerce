package com.ibs.spato.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivitySettingsBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.Utils
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    @Inject
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectSettings(this)
        binding.langSwitch.isChecked = session.getString(Constants.LANG_SETTING_KEY) == Constants.GERMAN

        if (session.getString(Constants.NOTIFICATION) == Constants.OFF){
            binding.onOffButton.setImageResource(R.drawable.off_notification)
        } else{
            binding.onOffButton.setImageResource(R.drawable.on_notification)
        }

        binding.onOffButton.setOnClickListener {
            if (session.getString(Constants.NOTIFICATION) == Constants.OFF){
                binding.onOffButton.setImageResource(R.drawable.on_notification)
                session.setString(Constants.NOTIFICATION, Constants.ON)
            } else{
                binding.onOffButton.setImageResource(R.drawable.off_notification)
                session.setString(Constants.NOTIFICATION, Constants.OFF)
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.langSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                session.setString(Constants.LANG_SETTING_KEY, Constants.GERMAN)
                startActivity(Intent(this, SplashActivity::class.java))
                finishAffinity()
            } else {
                session.setString(Constants.LANG_SETTING_KEY, Constants.ENGLISH)
                startActivity(Intent(this, SplashActivity::class.java))
                finishAffinity()
            }
        }
    }
}