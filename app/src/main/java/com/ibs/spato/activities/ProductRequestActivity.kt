package com.ibs.spato.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.dhaval2404.imagepicker.ImagePicker
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivityProductRequestBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.models.request_product.RequestProductModel
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import com.theartofdev.edmodo.cropper.CropImage
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ProductRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductRequestBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private lateinit var alertDialog: AlertDialog
    private lateinit var alertView: View
    private lateinit var cancel: TextView
    private lateinit var goToSetting: TextView
    private var image: String = ""
    private lateinit var savedUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectProductRequest(this)
        apiCall()
        onClick()
        observe()
        dialog()
    }

    private fun apiCall() {
        mainViewModel.customerDetails()
    }

    private fun dialog() {
        alertDialog = AlertDialog.Builder(this, R.style.CustomDialog).create()
        alertView = LayoutInflater.from(this).inflate(R.layout.permission_custom_dialog_layout, null)
        cancel = alertView.findViewById(R.id.cancel)
        goToSetting = alertView.findViewById(R.id.goToSetting)

        cancel.setOnClickListener {
            alertDialog.dismiss()
        }

        goToSetting.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        mainViewModel.customerDetailsResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    binding.etFullName.setText("${it.data!!.data.first_name} ${it.data.data.last_name}")
                    binding.etPhoneNumber.setText(it.data.data.mobile)
                    binding.etEmail.setText(it.data.data.email)
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

        mainViewModel.commonResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, getString(R.string.requested_successfully), Toast.LENGTH_SHORT).show()
                    finish()
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

    private fun onClick() {
        binding.llSendRequest.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val sparePartName = binding.etSparePart.text.toString().trim()
            val sparePartDetails = binding.etSparePartDetails.text.toString().trim()

            if (fullName.isNullOrEmpty()) {
                binding.etFullName.requestFocus()
                binding.etFullName.error = getString(R.string.enter_your_name)
                return@setOnClickListener
            } else if (phoneNumber.isNullOrEmpty()) {
                binding.etPhoneNumber.requestFocus()
                binding.etPhoneNumber.error = getString(R.string.enter_your_mobile_number)
                return@setOnClickListener
            } else if (email.isNullOrEmpty()) {
                binding.etEmail.requestFocus()
                binding.etEmail.error = getString(R.string.enter_your_email_address)
                return@setOnClickListener
            } else if (!email.contains("@") || !email.contains(".com")) {
                binding.etEmail.requestFocus()
                binding.etEmail.error = getString(R.string.enter_a_valid_email)
                return@setOnClickListener
            } else if (sparePartName.isNullOrEmpty()) {
                binding.etSparePart.requestFocus()
                binding.etSparePart.error = getString(R.string.enter_spare_part_name)
                return@setOnClickListener
            } else {
                val requestProductModel = RequestProductModel(fullName, phoneNumber, email, sparePartName, sparePartDetails, image)
                mainViewModel.requestSparePart(requestProductModel)
            }
        }

        binding.llUpload.setOnClickListener {
            if (Utils.checkPermissions(this)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    pickImage()
                } else{
                    pickImageFromGallery()
                }
            } else{
                Utils.requestPermission(this)
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    pickImage()
                } else{
                    pickImageFromGallery()
                }
            } else {
                alertDialog.setView(alertView)
                alertDialog.show()
            }
        }
    }

    private fun pickImageFromGallery() {
        ImagePicker.with(this).crop(4f, 4f)                   //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
            .start(Constants.CHOOSE_IMAGE_REQUEST_CODE)
    }

    private fun pickImage() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        startActivityForResult(intent, Constants.CHOOSE_IMAGE_REQUEST_CODE_FOR_13)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CHOOSE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val uri: Uri = data.data!!
                    binding.productImage.setImageURI(uri)
                    val bitmap: Bitmap = (binding.productImage.drawable as BitmapDrawable).bitmap
                    image = Utils.bitmapToBase64(bitmap)
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Not selected", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == Constants.CHOOSE_IMAGE_REQUEST_CODE_FOR_13){
            if (data != null && resultCode == Activity.RESULT_OK) {
                val uri: Uri = data.data!!
                savedUri = uri
                CropImage.activity(uri).setAspectRatio(1,1).start(this)
            } else{
                Toast.makeText(this, getString(R.string.failed_try_again), Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                binding.productImage.setImageURI(resultUri)
                val bitmap: Bitmap = (binding.productImage.drawable as BitmapDrawable).bitmap
                image = Utils.bitmapToBase64(bitmap)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                val error = result.error
                binding.productImage.setImageURI(savedUri)
                val bitmap: Bitmap = (binding.productImage.drawable as BitmapDrawable).bitmap
                image = Utils.bitmapToBase64(bitmap)
            }
        }
    }
}