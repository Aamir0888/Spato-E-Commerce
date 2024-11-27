package com.ibs.spato.activities

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.adapters.SparePartsListAdapter
import com.ibs.spato.adapters.UserGuidePdfAdapter
import com.ibs.spato.databinding.ActivityProductDetailsBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.dashboard_product_list.Products
import com.ibs.spato.responses.product_details.Product
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.service.LoginService
import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class ProductDetailsActivity : AppCompatActivity(), SparePartsListAdapter.SpareItemsClickListener, UserGuidePdfAdapter.UserGuideClickListener {
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var sparePartsListAdapter: SparePartsListAdapter
    private lateinit var sparePartsList: ArrayList<Products>
    private lateinit var userGuidePdfAdapter: UserGuidePdfAdapter
    private lateinit var pdfList: ArrayList<String>
    private var productQuantity = 1
    private var productId: Long = 0
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    @Inject
    lateinit var session: Session
    private var similarApiCallCheck = true
    private var loading = true
    private var apiCallCount = 0
    private var showError = true
    private var inStock = true
    private var availableQuantity = 0
    private var specifications = ""
    private var descriptions = ""
    private lateinit var pdfUrl : String
    private lateinit var alertDialog: AlertDialog
    private lateinit var alertView: View
    private lateinit var cancel: TextView
    private lateinit var goToSetting: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectProductDetails(this)
        productId = intent.getLongExtra(Constants.PRODUCT_ID, 0)
        apiCall()
        observe()
        onClicks()
        dialog()
        descriptionClick()

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCallCount = 0
            showError = true
            similarApiCallCheck = true
            apiCall()
        }
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

    private fun observe() {
        mainViewModel.productDetailsResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    apiCallCount++
                    descriptionClick()
                    val product: Product = it.data!!.data.product
                    setProductDetails(product)
                    stopProgressBar(Constants.SUCCESS)
                }

                is NetworkResult.Error -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError){
                        showError = false
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.NoInternet -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError){
                        showError = false
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        mainViewModel.productListResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    apiCallCount++
                    if (it.data!!.data.products.size > 0){
                        sparePartsList = ArrayList()
                        sparePartsList = it.data.data.products
                        binding.spareParts.visibility = View.VISIBLE
                        binding.sparePartsDescription.visibility = View.VISIBLE
                        binding.sparePartsRecyclerView.visibility = View.VISIBLE
                        binding.sparePartsRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@ProductDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
                            sparePartsListAdapter = SparePartsListAdapter(this@ProductDetailsActivity, sparePartsList, this@ProductDetailsActivity)
                            adapter = sparePartsListAdapter
                        }
                    } else{
                        if (similarApiCallCheck){
                            mainViewModel.similarProductList(productId)
                            similarApiCallCheck = false
                        }
                        binding.spareParts.visibility = View.GONE
                        binding.sparePartsDescription.visibility = View.GONE
                        binding.sparePartsRecyclerView.visibility = View.GONE
                    }
                    stopProgressBar(Constants.SUCCESS)
                }

                is NetworkResult.Error -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError){
                        showError = false
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.NoInternet -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError){
                        showError = false
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        mainViewModel.addToCartResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, getString(R.string.added_successfully), Toast.LENGTH_SHORT).show()
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

    private fun setProductDetails(product: Product) {
        Utils.loadImage(this, product.image[0], binding.productImage)
        binding.productName.text = product.name

        if (product.specification.size == 0 && product.pdf.size == 0){
            binding.llDescriptionSpecificationsPdfs.visibility = View.GONE
        } else{
            binding.llDescriptionSpecificationsPdfs.visibility = View.VISIBLE
        }

        if (product.specification.size > 0){
            specifications = ""
//            binding.firstView.visibility = View.VISIBLE
            binding.llSpecifications.visibility = View.VISIBLE
            for (i in 0 until product.specification.size){
                specifications = if (i == product.specification.lastIndex){
                    specifications + (i+1).toString() +")  "+product.specification[i]
                } else{
                    specifications + (i+1).toString() +")  "+product.specification[i] + "\n\n"
                }
            }
        } else{
//            binding.firstView.visibility = View.GONE
            binding.llSpecifications.visibility = View.GONE
        }
        if (product.pdf.size > 0){
            pdfList = ArrayList()
//            binding.secondView.visibility = View.VISIBLE
            binding.llUserGuide.visibility = View.VISIBLE
            for (i in 0 until product.pdf.size){
                pdfList.add(product.pdf[i])
            }
        } else{
//            binding.secondView.visibility = View.GONE
            binding.llUserGuide.visibility = View.GONE
        }
        if (product.description == null || product.description == ""){
            binding.llProductDescription.visibility = View.GONE
        } else{
            binding.llProductDescription.visibility = View.VISIBLE
            binding.productDescription.text = product.description
            descriptions = product.description
        }
        if (product.price == null || product.price == ""){
            binding.productPrice.visibility = View.GONE
            binding.currency.visibility = View.GONE
        } else{
            binding.productPrice.visibility = View.VISIBLE
            binding.currency.visibility = View.VISIBLE
            binding.productPrice.text = product.price
        }
        inStock = product.is_in_stock
        availableQuantity = product.qty
    }

    private fun apiCall() {
        mainViewModel.productDetails(productId)
        mainViewModel.sparePartsList(productId)
    }

    private fun onClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.topView.setOnClickListener {
            //  do nothing
        }

        binding.addTv.setOnClickListener {
            if (productQuantity == availableQuantity){
                Toast.makeText(this, "${getString(R.string.only)} $availableQuantity ${getString(R.string.items_available)}", Toast.LENGTH_SHORT).show()
            } else{
                productQuantity++
                binding.productQuantity.text = productQuantity.toString()
            }
        }

        binding.subtractTv.setOnClickListener {
            if (productQuantity != 1){
                productQuantity--
                binding.productQuantity.text = productQuantity.toString()
            } else{
                Toast.makeText(this, getString(R.string.quantity_cant_be_less_than_one), Toast.LENGTH_SHORT).show()
            }
        }

        binding.llAddToCart.setOnClickListener {
            if (session.getString(Constants.LOGIN_TOKEN) == Constants.NO_DATA){
                startActivity(Intent(this, LoginActivity::class.java))
            } else{
                if (inStock){
                    mainViewModel.addToCart(productId, productQuantity, "simple")
                } else{
                    Toast.makeText(this, getString(R.string.product_is_not_available), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.llProductRequest.setOnClickListener {
            val intent = Intent(this, ProductRequestActivity::class.java)
            startActivity(intent)
        }

        binding.llDescription.setOnClickListener {
            descriptionClick()
        }
        binding.llSpecifications.setOnClickListener {
            specificationsClick()
        }
        binding.llUserGuide.setOnClickListener {
            userGuideClick()
        }
    }

    private fun stopProgressBar(status: String) {
        if (apiCallCount == 2) {
            spatoProgressBar.stop()
            binding.pullToRefresh.isRefreshing = false
            if (status == Constants.FAILURE){
                binding.topView.visibility = View.VISIBLE
                binding.noData.visibility = View.VISIBLE
                binding.constraintLayout.visibility = View.GONE
            } else if (status == Constants.SUCCESS){
                binding.topView.visibility = View.INVISIBLE
                binding.noData.visibility = View.INVISIBLE
                binding.constraintLayout.visibility = View.VISIBLE
                binding.scrollView.smoothScrollTo(0,0)
                binding.nestedScrollView.smoothScrollTo(0, 0)
            }
        }
    }

    override fun onClick(productId: Long) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.PRODUCT_ID, productId)
        startActivity(intent)
        finish()
    }

    private fun descriptionClick(){
        binding.productDescription.visibility = View.VISIBLE
        binding.userGuideRecyclerView.visibility = View.GONE
        binding.productDescription.text = descriptions

        binding.descriptionView.visibility = View.VISIBLE
        binding.specificationsView.visibility = View.GONE
        binding.userGuideView.visibility = View.GONE

        binding.tvDescription.setTextColor(ContextCompat.getColor(this, R.color.spato_primary_color))
        binding.tvSpecifications.setTextColor(ContextCompat.getColor(this, R.color.half_white))
        binding.tvUserGuide.setTextColor(ContextCompat.getColor(this, R.color.half_white))
    }
    private fun specificationsClick(){
        binding.productDescription.visibility = View.VISIBLE
        binding.userGuideRecyclerView.visibility = View.GONE
        binding.productDescription.text = specifications

        binding.specificationsView.visibility = View.VISIBLE
        binding.descriptionView.visibility = View.GONE
        binding.userGuideView.visibility = View.GONE

        binding.tvDescription.setTextColor(ContextCompat.getColor(this, R.color.half_white))
        binding.tvSpecifications.setTextColor(ContextCompat.getColor(this, R.color.spato_primary_color))
        binding.tvUserGuide.setTextColor(ContextCompat.getColor(this, R.color.half_white))
    }
    private fun userGuideClick(){
        binding.productDescription.visibility = View.GONE
        binding.userGuideRecyclerView.visibility = View.VISIBLE
        binding.userGuideRecyclerView.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@ProductDetailsActivity)
            userGuidePdfAdapter = UserGuidePdfAdapter(this@ProductDetailsActivity, pdfList, this@ProductDetailsActivity)
            adapter = userGuidePdfAdapter
        }

        binding.descriptionView.visibility = View.GONE
        binding.specificationsView.visibility = View.GONE
        binding.userGuideView.visibility = View.VISIBLE

        binding.tvDescription.setTextColor(ContextCompat.getColor(this, R.color.half_white))
        binding.tvSpecifications.setTextColor(ContextCompat.getColor(this, R.color.half_white))
        binding.tvUserGuide.setTextColor(ContextCompat.getColor(this, R.color.spato_primary_color))
    }
    override fun userGuideClick(pdf: String) {
        pdfUrl = pdf
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
            if (Utils.permissionCheck(this)){
                Utils.downloadPdf(pdf, this)
            } else{
                Utils.requestPermission(this)
            }
        } else{
            Utils.downloadPdf(pdf, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.downloadPdf(pdfUrl, this)
            } else {
                alertDialog.setView(alertView)
                alertDialog.show()
            }
        }
    }

}