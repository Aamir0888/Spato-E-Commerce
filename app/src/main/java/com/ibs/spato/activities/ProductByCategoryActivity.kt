package com.ibs.spato.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.adapters.CategoryListAdapterMoreLess
import com.ibs.spato.adapters.ProductListAdapter
import com.ibs.spato.databinding.ActivityProductByCategoryBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.dashboard_category_list.CategoryListResponse
import com.ibs.spato.responses.dashboard_product_list.Products
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class ProductByCategoryActivity : AppCompatActivity(), ProductListAdapter.ItemClickListener, CategoryListAdapterMoreLess.MoreClickListener{
    private lateinit var binding: ActivityProductByCategoryBinding
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var productList: ArrayList<Products>
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private lateinit var category: CategoryListResponse.Data.Category
    private var categoryId: Long = 0
    private var status: String = ""
    private lateinit var categoryListAdapter: CategoryListAdapterMoreLess
    private lateinit var categoryList: ArrayList<CategoryListResponse.Data.Category>
    private var add = true

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductByCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectProductsByCategory(this)
        status = intent.getStringExtra(Constants.PRODUCT_BY_CATEGORY_AND_SEARCH_KEY).toString()
        if (status == Constants.SEARCH){
            productList = intent.extras!!.getSerializable(Constants.PRODUCT_LIST) as ArrayList<Products>
            val keyword = intent.getStringExtra(Constants.KEY)
            binding.linearLayout.visibility = View.VISIBLE
            binding.categoryListRecyclerView.visibility = View.GONE
            binding.view.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            setProductList(productList)
            binding.pullToRefresh.isEnabled = false
            binding.productsBy.text = "${getString(R.string.products_by)} \"$keyword\""
        } else{
            category = intent.extras!!.getSerializable(Constants.CHILD_CATEGORY) as CategoryListResponse.Data.Category
            categoryId = category.id
            binding.productsBy.text = "${getString(R.string.products_by)} \"${category.name}\""
            apiCall()
            observe()
            binding.pullToRefresh.setOnRefreshListener {
                binding.pullToRefresh.isRefreshing = true
                apiCall()
            }
        }
        onClicks()
    }

    private fun onClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun observe() {
        mainViewModel.productListResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    if (it.data!!.data == null){
                        binding.linearLayout.visibility = View.INVISIBLE
                        binding.noData.visibility = View.VISIBLE
                    } else if (it.data.data.products.size > 0) {
                        binding.linearLayout.visibility = View.VISIBLE
                        binding.noData.visibility = View.INVISIBLE
                        productList = ArrayList()
                        productList = it.data.data.products
                        setProductList(productList)
                    } else {
                        binding.linearLayout.visibility = View.INVISIBLE
                        binding.noData.visibility = View.VISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.linearLayout.visibility = View.INVISIBLE
                    binding.noData.visibility = View.VISIBLE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.linearLayout.visibility = View.INVISIBLE
                    binding.noData.visibility = View.VISIBLE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        mainViewModel.categoryListResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    if (it.data!!.data == null){
                        binding.categoryListRecyclerView.visibility = View.GONE
                        binding.view.visibility = View.GONE
                    } else if (it.data.data.category.size > 0){
                        categoryList = ArrayList()
                        binding.categoryListRecyclerView.visibility = View.VISIBLE
                        binding.view.visibility = View.VISIBLE
                        categoryList = it.data.data.category
                        showCategory(categoryList, Constants.CATEGORY_LESS)
                    } else{
                        binding.categoryListRecyclerView.visibility = View.GONE
                        binding.view.visibility = View.GONE
                    }
                }

                is NetworkResult.Error -> {
                    binding.categoryListRecyclerView.visibility = View.GONE
                    binding.view.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    binding.categoryListRecyclerView.visibility = View.GONE
                    binding.view.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setProductList(productList: ArrayList<Products>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProductByCategoryActivity)
            productListAdapter = ProductListAdapter(this@ProductByCategoryActivity, productList, this@ProductByCategoryActivity)
            adapter = productListAdapter
        }
    }

    private fun apiCall() {
        mainViewModel.productsByCategory(categoryId)
        mainViewModel.childCategories(categoryId)
    }

    override fun onClick(productId: Long) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.PRODUCT_ID, productId)
        startActivity(intent)
    }

    override fun lessClicked() {
        showCategory(categoryList, Constants.CATEGORY_LESS)
    }

    override fun moreClicked() {
        if (add) {
            add = false
            categoryList.add(CategoryListResponse.Data.Category("", 0, ""))
        }
        showCategory(categoryList, Constants.CATEGORY_MORE)
    }

    private fun showCategory(list: ArrayList<CategoryListResponse.Data.Category>, category: String) {
        binding.categoryListRecyclerView.apply {
            layoutManager = GridLayoutManager(this@ProductByCategoryActivity, 5)
            categoryListAdapter = CategoryListAdapterMoreLess(this@ProductByCategoryActivity, list, this@ProductByCategoryActivity, category)
            adapter = categoryListAdapter
        }
    }

    override fun categoryItemClicked(category: CategoryListResponse.Data.Category) {
        val intent = Intent(this, ProductByCategoryActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(Constants.CHILD_CATEGORY, category)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}