package com.ibs.spato.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivityChoosePaymentMethodBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class ChoosePaymentMethodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChoosePaymentMethodBinding
    private var selectedPaymentMethod = ""
    private var selectedAddressId: Long = 0
    private var cartId: Long = 0
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosePaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectChoosePaymentMethod(this)
        selectedAddressId = intent.getLongExtra(Constants.ADDRESS_ID, 0)
        cartId = intent.getLongExtra(Constants.CART_ID, 0)
        binding.totalAmount.text = intent.getDoubleExtra(Constants.TOTAL_AMOUNT,0.0).toString()
        onClicks()
        observe()
    }

    private fun onClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.checkoutSuccessful.topLayout.setOnClickListener {
            //  do nothing
        }
        binding.checkoutSuccessful.llGoToHome.setOnClickListener {
            onBackPressed()
        }

        binding.llBankTransfer.setOnClickListener {
            selectedPaymentMethod = "Bank Transfer"
            binding.llBankTransfer.setBackgroundResource(R.drawable.selected_payment_method_background)
            binding.llPayPal.setBackgroundResource(R.drawable.spato_custom_edittext_background)
            binding.llCashOnDelivery.setBackgroundResource(R.drawable.spato_custom_edittext_background)
            if (!binding.bankTransferVisibleInvisible.isVisible){
                Utils.expand(binding.bankTransferVisibleInvisible)
                binding.bankArrow.animate().rotation(270f).start()
            } else{
                Utils.collapse(binding.bankTransferVisibleInvisible)
                binding.bankArrow.animate().rotation(90f).start()
            }
        }

        binding.llPayPal.setOnClickListener {
            selectedPaymentMethod = "PayPal"
            binding.llBankTransfer.setBackgroundResource(R.drawable.spato_custom_edittext_background)
            binding.llPayPal.setBackgroundResource(R.drawable.selected_payment_method_background)
            binding.llCashOnDelivery.setBackgroundResource(R.drawable.spato_custom_edittext_background)
            if (!binding.paypalVisibleInvisible.isVisible){
                Utils.expand(binding.paypalVisibleInvisible)
                binding.paypalArrow.animate().rotation(270f).start()
            } else{
                Utils.collapse(binding.paypalVisibleInvisible)
                binding.paypalArrow.animate().rotation(90f).start()
            }
        }

        binding.llCashOnDelivery.setOnClickListener {
            selectedPaymentMethod = "Cash On Delivery"
            binding.llBankTransfer.setBackgroundResource(R.drawable.spato_custom_edittext_background)
            binding.llPayPal.setBackgroundResource(R.drawable.spato_custom_edittext_background)
            binding.llCashOnDelivery.setBackgroundResource(R.drawable.selected_payment_method_background)
        }

        binding.llCheckout.setOnClickListener {
            if (selectedPaymentMethod == ""){
                Toast.makeText(this, getString(R.string.please_select_payment_method), Toast.LENGTH_SHORT).show()
            } else{
                apiCall()
            }
        }
    }

    private fun apiCall() {
        mainViewModel.orderPlace(selectedAddressId, cartId, "cashondelivery")
    }

    private fun observe() {
        mainViewModel.orderPlaceResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    binding.checkoutSuccessful.root.visibility = View.VISIBLE
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onBackPressed() {
        if (binding.checkoutSuccessful.root.isVisible){
            val intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        } else{
            super.onBackPressed()
        }
    }
}