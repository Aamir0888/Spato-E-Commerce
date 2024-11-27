package com.ibs.spato.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivityLoginBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.service.LoginService
import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import java.util.Locale
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var boolean: Boolean = false
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    @Inject
    lateinit var session: Session
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectLogin(this)
        session.setString(Constants.LOGIN_SCREEN_ACTIVE_OR_NOT_KEY, Constants.ACTIVE)

        status = intent.getStringExtra(Constants.KEY).toString()
        if (status == Constants.VALUE){
            binding.skip.visibility = View.INVISIBLE
        } else{
            binding.skip.visibility = View.VISIBLE
        }
        binding.langSwitch.isChecked = session.getString(Constants.LANG_SETTING_KEY) == Constants.GERMAN
        onClicks()

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),101)
            }
        }
    }

    private fun onClicks() {
        binding.llLogin.setOnClickListener {
            if (binding.etEmail.text.toString().trim().isNullOrEmpty()) {
                binding.etEmail.requestFocus()
                binding.etEmail.error = getString(R.string.enter_your_email_address)
                return@setOnClickListener
            } else if (!binding.etEmail.text.toString().trim().contains("@gmail.com")) {
                binding.etEmail.requestFocus()
                binding.etEmail.error = getString(R.string.enter_a_valid_email)
                return@setOnClickListener
            } else if (binding.etPassword.text.toString().trim().isNullOrEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = getString(R.string.enter_your_password)
                return@setOnClickListener
            } else if (binding.etPassword.text.toString().trim().length < 6) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = getString(R.string.password_must_be_minimum_6_characters)
                return@setOnClickListener
            } else{
                mainViewModel.loginUser(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
            }
        }

        mainViewModel.loginResponse.observe(this, Observer {
            when(it){
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }
                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, getString(R.string.login_successfully), Toast.LENGTH_SHORT).show()
                    session.setString(Constants.LOGIN_TOKEN, it.data.toString())
                    startActivity(Intent(this, DashboardActivity::class.java))
                    stopService(Intent(this, LoginService::class.java))
                    finishAffinity()
                }
                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })


        binding.passwordHideUnhide.setOnClickListener {
            if (boolean) {
                boolean = false
                binding.passwordHideUnhide.setImageResource(R.drawable.password_hide)
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.etPassword.setSelection(binding.etPassword.length())
                binding.etPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
            } else {
                boolean = true
                binding.passwordHideUnhide.setImageResource(R.drawable.password_unhide)
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.etPassword.setSelection(binding.etPassword.length())
                binding.etPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
            }
        }
        binding.register.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        binding.skip.setOnClickListener {
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

    override fun onDestroy() {
        super.onDestroy()
        session.setString(Constants.LOGIN_SCREEN_ACTIVE_OR_NOT_KEY, Constants.INACTIVE)
    }

    override fun onBackPressed() {
        if (status == Constants.VALUE){
            finishAffinity()
        } else{
            finish()
        }
    }
}