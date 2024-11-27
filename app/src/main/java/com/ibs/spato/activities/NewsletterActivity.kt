package com.ibs.spato.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.adapters.NewslettersAdapter
import com.ibs.spato.adapters.NotificationListAdapter
import com.ibs.spato.databinding.ActivityNewsletterBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.newsletter_list.NewsletterListResponse
import com.ibs.spato.responses.notification_list.NotificationListResponse
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class NewsletterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsletterBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private lateinit var newslettersAdapter: NewslettersAdapter
    private lateinit var newsletterList: ArrayList<NewsletterListResponse.Data>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsletterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectNewsletters(this)
        apiCall()
        observe()
        onClicks()
        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCall()
        }
    }

    private fun onClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun observe() {
        mainViewModel.newsletterListResponse.observe(this, Observer {
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
                    if (it.data!!.data.size > 0){
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.noData.visibility = View.INVISIBLE
                        newsletterList = it.data.data
                        binding.recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@NewsletterActivity)
                            newslettersAdapter = NewslettersAdapter(this@NewsletterActivity, newsletterList)
                            adapter = newslettersAdapter
                        }
                    } else{
                        binding.recyclerView.visibility = View.INVISIBLE
                        binding.noData.visibility = View.VISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.noData.visibility = View.VISIBLE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.noData.visibility = View.VISIBLE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun apiCall() {
        mainViewModel.newsletters()
    }
}