package com.ibs.spato.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.activities.ProductByCategoryActivity
import com.ibs.spato.activities.ProductDetailsActivity
import com.ibs.spato.adapters.CategoryListAdapter
import com.ibs.spato.adapters.DashboardTopProductListAdapter
import com.ibs.spato.adapters.ProductListAdapter
import com.ibs.spato.databinding.FragmentHomeBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.dashboard_category_list.CategoryListResponse
import com.ibs.spato.responses.dashboard_product_list.Products
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class HomeFragment : Fragment(), CategoryListAdapter.MoreClickListener,
    DashboardTopProductListAdapter.ItemClickListener, ProductListAdapter.ItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dashboardTopProductListAdapter: DashboardTopProductListAdapter
    private lateinit var topProductList: ArrayList<Products>
    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var categoryList: ArrayList<CategoryListResponse.Data.Category>
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var productList: ArrayList<Products>

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private var apiCallCount = 0
    private var showError = true
    private var keyword: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        ApplicationComponentObject.create(requireActivity()).injectHomeFragment(this)
        apiCall()
        observe()
        onClicks()

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCallCount = 0
            showError = true
            apiCall()
        }

        return binding.root
    }

    private fun onClicks() {
        binding.topView.setOnClickListener {
            //  do nothing
        }
        binding.searchButton.setOnClickListener {
            if (keyword == "" || keyword == null){
                Toast.makeText(activity, getString(R.string.please_type_something), Toast.LENGTH_SHORT).show()
            } else{
                mainViewModel.searchProduct(keyword)
                loading = true
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (keyword == "" || keyword == null){
                    Toast.makeText(activity, getString(R.string.please_type_something), Toast.LENGTH_SHORT).show()
                } else{
                    mainViewModel.searchProduct(keyword)
                    loading = true
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                keyword = newText.toString()
                return true
            }
        })
    }

    private fun observe() {
        mainViewModel.topProductListResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    apiCallCount++
                    if (it.data!!.data == null){
                        binding.topProductRecyclerView.visibility = View.GONE
                    } else if (it.data.data.products.size > 0) {
                        binding.topProductRecyclerView.visibility = View.VISIBLE
                        topProductList = ArrayList()
                        topProductList = it.data.data.products
                        if (isAdded){
                            binding.topProductRecyclerView.apply {
                                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                                dashboardTopProductListAdapter = DashboardTopProductListAdapter(requireContext(), topProductList, this@HomeFragment)
                                adapter = dashboardTopProductListAdapter
                            }
                        }
                    } else {
                        binding.topProductRecyclerView.visibility = View.GONE
                    }
                    stopProgressBar(Constants.SUCCESS)
                }

                is NetworkResult.Error -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError) {
                        showError = false
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.NoInternet -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError) {
                        showError = false
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        mainViewModel.categoryListResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    apiCallCount++
                    if (it.data!!.data.category.size > 0) {
                        categoryList = ArrayList()
                        binding.categoryListRecyclerView.visibility = View.VISIBLE
                        binding.view.visibility = View.VISIBLE
                        categoryList = it.data.data.category
                        if (isAdded){
                            showCategory(categoryList)
                        }
                        stopProgressBar(Constants.SUCCESS)
                    } else {
                        binding.categoryListRecyclerView.visibility = View.GONE
                        binding.view.visibility = View.GONE
                    }
                }

                is NetworkResult.Error -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError) {
                        showError = false
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.NoInternet -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError) {
                        showError = false
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        mainViewModel.productListResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    apiCallCount++
                    if (it.data!!.data.products.size > 0) {
                        productList = ArrayList()
                        productList = it.data.data.products
                        if (isAdded){
                            binding.productListRecyclerView.apply {
                                layoutManager = LinearLayoutManager(activity)
                                productListAdapter = ProductListAdapter(requireActivity(), productList,this@HomeFragment)
                                adapter = productListAdapter
                            }
                        }
                        stopProgressBar(Constants.SUCCESS)
                    }
                    else {

                    }
                }

                is NetworkResult.Error -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError) {
                        showError = false
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.NoInternet -> {
                    apiCallCount++
                    stopProgressBar(Constants.FAILURE)
                    if (showError) {
                        showError = false
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        mainViewModel.productsBySearchResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    if (it.data!!.data == null){
                        Toast.makeText(activity, getString(R.string.no_product_found), Toast.LENGTH_SHORT).show()
                    } else if (it.data.data.products.size > 0) {
                        productList = ArrayList()
                        productList = it.data.data.products
                        val intent = Intent(activity, ProductByCategoryActivity::class.java)
                        val bundle = Bundle()
                        intent.putExtra(Constants.PRODUCT_BY_CATEGORY_AND_SEARCH_KEY, Constants.SEARCH)
                        intent.putExtra(Constants.KEY, keyword)
                        bundle.putSerializable(Constants.PRODUCT_LIST, productList)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(activity, getString(R.string.no_product_found), Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun stopProgressBar(status: String) {
        if (apiCallCount == 3) {
            spatoProgressBar.stop()
            binding.pullToRefresh.isRefreshing = false
            if (status == Constants.FAILURE) {
                binding.topView.visibility = View.VISIBLE
                binding.noData.visibility = View.VISIBLE
            } else if (status == Constants.SUCCESS) {
                binding.topView.visibility = View.INVISIBLE
                binding.noData.visibility = View.INVISIBLE
            }
        }
    }

    private fun apiCall() {
        mainViewModel.topProductList()
        mainViewModel.categoryList()
        mainViewModel.productList(1)
    }

    private fun showCategory(list: ArrayList<CategoryListResponse.Data.Category>) {
        binding.categoryListRecyclerView.apply {
            layoutManager = GridLayoutManager(activity, 5)
            categoryListAdapter = CategoryListAdapter(requireActivity(), list, this@HomeFragment)
            adapter = categoryListAdapter
        }
    }

    override fun topItemClicked(productId: Long) {
        productDetailsActivity(productId)
    }

    override fun onClick(productId: Long) {
        productDetailsActivity(productId)
    }

    override fun categoryItemClicked(category: CategoryListResponse.Data.Category) {
        val intent = Intent(activity, ProductByCategoryActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(Constants.CHILD_CATEGORY, category)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun productDetailsActivity(productId: Long) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.PRODUCT_ID, productId)
        startActivity(intent)
    }

    override fun onDetach() {
        super.onDetach()
        spatoProgressBar.stop()
    }
}