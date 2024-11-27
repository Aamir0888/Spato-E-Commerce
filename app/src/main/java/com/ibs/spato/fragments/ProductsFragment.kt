package com.ibs.spato.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.activities.ProductByCategoryActivity
import com.ibs.spato.activities.ProductDetailsActivity
import com.ibs.spato.adapters.CategoryListAdapter
import com.ibs.spato.adapters.ProductListAdapter
import com.ibs.spato.databinding.FragmentProductBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.dashboard_category_list.CategoryListResponse
import com.ibs.spato.responses.dashboard_product_list.Products
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class ProductsFragment : Fragment(), ProductListAdapter.ItemClickListener, CategoryListAdapter.MoreClickListener {
    private lateinit var binding: FragmentProductBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var productList: ArrayList<Products>
    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var categoryList: ArrayList<CategoryListResponse.Data.Category>
    private var apiCallCount = 0
    private var showError = true
    private var add = true
    private var page = 1
    private var isPaginationAllowed: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProductBinding.inflate(inflater, container, false)

        ApplicationComponentObject.create(requireContext()).injectProductFragment(this)
        apiCall()
        observe()
        binding.productListRecyclerView.setHasFixedSize(true)
        binding.productListRecyclerView.layoutManager = LinearLayoutManager(activity)
        productList = ArrayList()
        setupPagination()

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCallCount = 0
            showError = true
            apiCall()
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        mainViewModel.productListResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                    binding.mProgressBar.visibility = View.VISIBLE
                }

                is NetworkResult.Success -> {
                    apiCallCount++
                    binding.mProgressBar.visibility = View.GONE
                    if (it.data!!.data == null){
                        isPaginationAllowed = false
                    } else if (it.data.data.products.size > 0) {
                        productList.addAll(it.data.data.products)
                        if (isAdded){
                            binding.productListRecyclerView.apply {
                                productListAdapter = ProductListAdapter(requireContext(), productList, this@ProductsFragment)
                                adapter = productListAdapter
                                productListAdapter.notifyDataSetChanged()
                            }
                        }
                        stopProgressBar(Constants.SUCCESS)
                    } else {
                        stopProgressBar(Constants.FAILURE)
                    }
                }

                is NetworkResult.Error -> {
                    apiCallCount++
                    binding.mProgressBar.visibility = View.GONE
                    stopProgressBar(Constants.FAILURE)
                    if (showError) {
                        showError = false
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.NoInternet -> {
                    apiCallCount++
                    binding.mProgressBar.visibility = View.GONE
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
                        stopProgressBar(Constants.FAILURE)
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
    }

    private fun apiCall() {
        mainViewModel.productList(page)
        mainViewModel.categoryList()
    }

    private fun setupPagination() {
        if (isPaginationAllowed) {
            binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    mainViewModel.productList(++page)
                }
            })
        }
    }

    private fun stopProgressBar(status: String) {
        if (apiCallCount == 2) {
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

    override fun onClick(productId: Long) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.PRODUCT_ID, productId)
        startActivity(intent)
    }

    private fun showCategory(list: ArrayList<CategoryListResponse.Data.Category>) {
        binding.categoryListRecyclerView.apply {
            layoutManager = GridLayoutManager(activity, 5)
            categoryListAdapter = CategoryListAdapter(requireActivity(), list, this@ProductsFragment)
            adapter = categoryListAdapter
        }
    }

    override fun onDetach() {
        super.onDetach()
        spatoProgressBar.stop()
    }

    override fun categoryItemClicked(category: CategoryListResponse.Data.Category) {
        val intent = Intent(activity, ProductByCategoryActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(Constants.CHILD_CATEGORY, category)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}