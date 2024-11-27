package com.ibs.spato.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivityOtpVerificationBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class OtpVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerificationBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private lateinit var email: String
    private var pageStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pageStatus = intent.getStringExtra(Constants.OTP_VERIFICATION_KEY).toString()
        email = intent.getStringExtra(Constants.EMAIL).toString()
        binding.email.text = email
        ApplicationComponentObject.create(this).injectOtpVerification(this)
        onClicks()
    }

    private fun onClicks() {
        binding.resend.setOnClickListener {
            mainViewModel.forgotPassword(email)
        }

        binding.llDone.setOnClickListener {
            if (binding.pinView.length() != 4) {
                binding.pinView.error = getString(R.string.enter_otp)
                binding.pinView.requestFocus()
                return@setOnClickListener
            } else{
                mainViewModel.otpVerification(email, binding.pinView.text.toString().toInt(), "login")
            }
        }

        mainViewModel.commonResponse.observe(this, Observer {
            when (it){
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }
                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    if (pageStatus == Constants.SIGNUP_OTP){
                        Toast.makeText(this, getString(R.string.account_created_successfully), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    } else{
                        Toast.makeText(this, getString(R.string.otp_has_been_verified), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ChangePasswordActivity::class.java)
                        intent.putExtra(Constants.EMAIL, email)
                        startActivity(intent)
                    }
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

        mainViewModel.forgotPasswordResponse.observe(this, Observer {
            when(it){
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }
                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, getString(R.string.otp_has_been_sent), Toast.LENGTH_SHORT).show()
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