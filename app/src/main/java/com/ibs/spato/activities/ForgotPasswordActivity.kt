package com.ibs.spato.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.Observer
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivityForgotPasswordBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectForgotPassword(this)
        onClicks()
    }

    private fun onClicks() {
        binding.llNext.setOnClickListener {
            if (binding.etEmail.text.toString().trim().isNullOrEmpty()) {
                binding.etEmail.requestFocus()
                binding.etEmail.error = getString(R.string.enter_your_email_address)
                return@setOnClickListener
            } else if (!binding.etEmail.text.toString().trim().contains("@gmail.com")) {
                binding.etEmail.requestFocus()
                binding.etEmail.error = getString(R.string.enter_a_valid_email)
                return@setOnClickListener
            } else{
                email = binding.etEmail.text.toString().trim()
                mainViewModel.forgotPassword(email)
            }
        }

        mainViewModel.forgotPasswordResponse.observe(this, Observer {
            when(it){
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }
                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, getString(R.string.otp_has_been_sent), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, OtpVerificationActivity::class.java)
                    intent.putExtra(Constants.EMAIL, email)
                    startActivity(intent)
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

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}