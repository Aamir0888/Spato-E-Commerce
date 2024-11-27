package com.ibs.spato.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.lifecycle.Observer
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivitySignupBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var boolean: Boolean = false

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectSignup(this)
        onClicks()
    }

    private fun onClicks() {

        binding.llCreateAccount.setOnClickListener {
            if (binding.etFirstName.text.toString().trim().isNullOrEmpty()) {
                binding.etFirstName.requestFocus()
                binding.etFirstName.error = getString(R.string.enter_your_first_name)
                return@setOnClickListener
            } else if (binding.etLastName.text.toString().trim().isNullOrEmpty()) {
                binding.etLastName.requestFocus()
                binding.etLastName.error = getString(R.string.enter_your_last_name)
                return@setOnClickListener
            } else if (binding.etEmail.text.toString().trim().isNullOrEmpty()) {
                binding.etEmail.requestFocus()
                binding.etEmail.error = getString(R.string.enter_your_email_address)
                return@setOnClickListener
            } else if (!binding.etEmail.text.toString().trim().contains("@gmail.com")) {
                binding.etEmail.requestFocus()
                binding.etEmail.error = getString(R.string.enter_a_valid_email)
                return@setOnClickListener
            } else if (binding.etMobileNumber.text.toString().trim().isNullOrEmpty()) {
                binding.etMobileNumber.requestFocus()
                binding.etMobileNumber.error = getString(R.string.enter_your_mobile_number)
                return@setOnClickListener
            } else if (binding.etPassword.text.toString().trim().isNullOrEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = getString(R.string.enter_your_password)
                return@setOnClickListener
            } else if (binding.etPassword.text.toString().trim().length < 6) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = getString(R.string.password_must_be_minimum_6_characters)
                return@setOnClickListener
            } else {
                mainViewModel.registerCustomer(
                    binding.etFirstName.text.toString().trim(),
                    binding.etLastName.text.toString().trim(),
                    binding.etEmail.text.toString().trim(),
                    binding.etMobileNumber.text.toString().trim(),
                    binding.etPassword.text.toString().trim()
                )
            }
        }

        mainViewModel.registerResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, getString(R.string.otp_has_been_sent), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, OtpVerificationActivity::class.java)
                    intent.putExtra(Constants.EMAIL, binding.etEmail.text.toString().trim())
                    intent.putExtra(Constants.OTP_VERIFICATION_KEY, Constants.SIGNUP_OTP)
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
        binding.login.setOnClickListener {
            finish()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}