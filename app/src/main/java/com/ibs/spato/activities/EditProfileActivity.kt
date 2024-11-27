package com.ibs.spato.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.dhaval2404.imagepicker.ImagePicker
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivityEditProfileBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.customer_details.CustomerDetailsData
import com.ibs.spato.responses.models.customer_profile_pic.CustomerProfilePicModel
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import com.theartofdev.edmodo.cropper.CropImage
import javax.inject.Inject

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var boolean: Boolean = false
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private lateinit var customerData: CustomerDetailsData
    private lateinit var alertDialog: AlertDialog
    private lateinit var alertView: View
    private lateinit var cancel: TextView
    private lateinit var goToSetting: TextView
    private lateinit var savedUri: Uri

    companion object{
        var profileEdited = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectEditProfile(this)
        customerData = intent.extras!!.getSerializable(Constants.CUSTOMER_DATA) as CustomerDetailsData
        binding.etFirstName.setText(customerData.first_name)
        binding.etLastName.setText(customerData.last_name)
        binding.etMobileNumber.setText(customerData.mobile)
        binding.etEmail.text = customerData.email
        onClicks()
        observe()
        dialog()
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

    private fun onClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.editProfilePic.setOnClickListener {
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
        binding.passwordHideUnhide.setOnClickListener {
            hideUnhidePassword()
        }
        binding.confirmPasswordHideUnhide.setOnClickListener {
            hideUnhidePassword()
        }
        binding.llUpdateProfile.setOnClickListener {
            if (binding.etPassword.text.toString().trim().isNotBlank()) {
                if (binding.etPassword.text.toString().trim().length < 6) {
                    binding.etPassword.requestFocus()
                    binding.etPassword.error = getString(R.string.password_must_be_minimum_6_characters)
                    return@setOnClickListener
                } else if (binding.etConfirmPassword.text.toString().trim() != binding.etPassword.text.toString().trim()){
                    binding.etConfirmPassword.error = getString(R.string.password_and_confirm_password_are_not_same)
                    binding.etConfirmPassword.requestFocus()
                    return@setOnClickListener
                }
            }
            mainViewModel.editProfile(binding.etFirstName.text.toString().trim(),
                binding.etLastName.text.toString().trim(),
                binding.etMobileNumber.text.toString().trim(),
                binding.etPassword.text.toString().trim())
        }
    }

    private fun observe() {
        mainViewModel.editProfileResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, getString(R.string.profile_edited_successfully), Toast.LENGTH_SHORT).show()
                    profileEdited = true
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

        mainViewModel.commonResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
//                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
//                    spatoProgressBar.stop()
//                    Toast.makeText(this, getString(R.string.profile_pic_edited_successfully), Toast.LENGTH_SHORT).show()
                    profileEdited = true
                }

                is NetworkResult.Error -> {
//                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
//                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun hideUnhidePassword() {
        if (boolean) {
            boolean = false
            binding.passwordHideUnhide.setImageResource(R.drawable.password_hide)
            binding.confirmPasswordHideUnhide.setImageResource(R.drawable.password_hide)
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etPassword.setSelection(binding.etPassword.length())
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.length())
            binding.etPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
            binding.etConfirmPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
        } else {
            boolean = true
            binding.passwordHideUnhide.setImageResource(R.drawable.password_unhide)
            binding.confirmPasswordHideUnhide.setImageResource(R.drawable.password_unhide)
            binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etPassword.setSelection(binding.etPassword.length())
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.length())
            binding.etPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
            binding.etConfirmPassword.typeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun pickImage() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        startActivityForResult(intent, Constants.CHOOSE_IMAGE_REQUEST_CODE_FOR_13)
    }

    private fun pickImageFromGallery() {
        ImagePicker.with(this).crop(4f, 4f)                   //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
            .start(Constants.CHOOSE_IMAGE_REQUEST_CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CHOOSE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val uri: Uri = data.data!!
                    binding.pic.visibility = View.GONE
                    binding.editProfilePic.setImageURI(uri)
                    val bitmap: Bitmap = (binding.editProfilePic.drawable as BitmapDrawable).bitmap
                    val customerProfilePicModel = CustomerProfilePicModel(Utils.bitmapToBase64(bitmap))
                    mainViewModel.editProfilePic(customerProfilePicModel)
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
                binding.pic.visibility = View.GONE
                binding.editProfilePic.setImageURI(resultUri)
                val bitmap: Bitmap = (binding.editProfilePic.drawable as BitmapDrawable).bitmap
                val customerProfilePicModel = CustomerProfilePicModel(Utils.bitmapToBase64(bitmap))
                mainViewModel.editProfilePic(customerProfilePicModel)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                val error = result.error
                binding.pic.visibility = View.GONE
                binding.editProfilePic.setImageURI(savedUri)
                val bitmap: Bitmap = (binding.editProfilePic.drawable as BitmapDrawable).bitmap
                val customerProfilePicModel = CustomerProfilePicModel(Utils.bitmapToBase64(bitmap))
                mainViewModel.editProfilePic(customerProfilePicModel)
            }
        }
    }
}