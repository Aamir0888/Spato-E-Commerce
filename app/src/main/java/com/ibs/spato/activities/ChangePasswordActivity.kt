package com.ibs.spato.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.lifecycle.Observer
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivityChangePasswordBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private var boolean: Boolean = false
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra(Constants.EMAIL).toString()
        ApplicationComponentObject.create(this).injectChangePassword(this)
        onClicks()
    }

    private fun onClicks() {
        binding.llUpdate.setOnClickListener {
            if (binding.etNewPassword.text.toString().trim().isNullOrEmpty()) {
                binding.etNewPassword.requestFocus()
                binding.etNewPassword.error = getString(R.string.enter_a_new_password)
                return@setOnClickListener
            } else if (binding.etNewPassword.text.toString().trim().length < 6) {
                binding.etNewPassword.requestFocus()
                binding.etNewPassword.error = getString(R.string.password_must_be_minimum_6_characters)
                return@setOnClickListener
            } else if (binding.etConfirmPassword.text.toString().trim().isNullOrEmpty()) {
                binding.etConfirmPassword.requestFocus()
                binding.etConfirmPassword.error = getString(R.string.confirm_new_password)
                return@setOnClickListener
            } else if (binding.etConfirmPassword.text.toString().trim() != binding.etNewPassword.text.toString().trim()){
                binding.etConfirmPassword.error = getString(R.string.password_and_confirm_password_are_not_same)
                binding.etConfirmPassword.requestFocus()
                return@setOnClickListener
            } else{
                mainViewModel.resetPassword(email, binding.etNewPassword.text.toString().trim())
            }
        }

        mainViewModel.commonResponse.observe(this, Observer {
            when(it){
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }
                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
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


        binding.newPasswordHideUnhide.setOnClickListener {
            hideUnhidePassword()
        }
        binding.confirmPasswordHideUnhide.setOnClickListener {
            hideUnhidePassword()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun hideUnhidePassword() {
        if (boolean) {
            boolean = false
            binding.newPasswordHideUnhide.setImageResource(R.drawable.password_hide)
            binding.confirmPasswordHideUnhide.setImageResource(R.drawable.password_hide)
            binding.etNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etNewPassword.setSelection(binding.etNewPassword.length())
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.length())
            binding.etNewPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
            binding.etConfirmPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
        } else {
            boolean = true
            binding.newPasswordHideUnhide.setImageResource(R.drawable.password_unhide)
            binding.confirmPasswordHideUnhide.setImageResource(R.drawable.password_unhide)
            binding.etNewPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etNewPassword.setSelection(binding.etNewPassword.length())
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.length())
            binding.etNewPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
            binding.etConfirmPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
        }
    }
}