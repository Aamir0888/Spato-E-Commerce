package com.ibs.spato.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.ibs.spato.databinding.ActivityCompanyPolicyBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.privacy_policy_terms_conditions.PrivacyPolicy
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class CompanyPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompanyPolicyBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectCompanyPolicy(this)
        apiCall()
        observe()

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCall()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.topView.setOnClickListener {
            //  do nothing
        }
    }

    private fun observe() {
        mainViewModel.policyResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    val data: PrivacyPolicy = it.data!!.data.privacy_policy

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.policy.text = Html.fromHtml(data.content, Html.FROM_HTML_MODE_LEGACY)
                        binding.policy.movementMethod = LinkMovementMethod.getInstance()
//                        Linkify.addLinks(binding.policy, Linkify.WEB_URLS)
                    } else {
                        binding.policy.text = Html.fromHtml(data.content)
                        binding.policy.movementMethod = LinkMovementMethod.getInstance()
//                        Linkify.addLinks(binding.policy, Linkify.WEB_URLS)
                    }
                    stopProgressBar(Constants.SUCCESS)
                }

                is NetworkResult.Error -> {
                    stopProgressBar(Constants.FAILURE)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    stopProgressBar(Constants.FAILURE)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun apiCall() {
        mainViewModel.companyPolicy()
    }

    private fun stopProgressBar(status: String) {
        spatoProgressBar.stop()
        binding.pullToRefresh.isRefreshing = false
        if (status == Constants.FAILURE){
            binding.topView.visibility = View.VISIBLE
            binding.noData.visibility = View.VISIBLE
        } else if (status == Constants.SUCCESS){
            binding.topView.visibility = View.INVISIBLE
            binding.noData.visibility = View.INVISIBLE
        }
    }
}