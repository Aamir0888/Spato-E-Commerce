package com.ibs.spato.activities

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.adapters.NotificationListAdapter
import com.ibs.spato.databinding.ActivityNotificationsBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.notification_list.NotificationListResponse
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.service.LoginService
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private lateinit var notificationListAdapter: NotificationListAdapter
    private lateinit var notificationList: ArrayList<NotificationListResponse.Data>
    private var liveDataObserver = false
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectNotifications(this)
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
        mainViewModel.notificationListResponse.observe(this, Observer {
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
                        notificationList = it.data.data
                        liveDataObserver = true
                        binding.recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@NotificationsActivity)
                            notificationListAdapter = NotificationListAdapter(this@NotificationsActivity, notificationList)
                            adapter = notificationListAdapter
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

        Utils.swipeToDelete(this, binding.recyclerView, R.drawable.delete_in_red, R.color.transparent)
        Utils.position.observe(this, Observer {
            if (liveDataObserver){
                mainViewModel.deleteNotification(notificationList[it].entity_id)
                position = it
            }
        })

        mainViewModel.commonResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    notificationList.remove(notificationList[position])
                    notificationListAdapter.notifyItemRemoved(position)
                    notificationListAdapter.notifyItemRangeChanged(position, notificationList.size)
                    if (notificationList.isEmpty() || notificationList.size == 0) {
                        binding.noData.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.INVISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    notificationListAdapter.notifyDataSetChanged()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    notificationListAdapter.notifyDataSetChanged()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun apiCall() {
        mainViewModel.notificationList()
    }

    override fun onDestroy() {
        super.onDestroy()
        liveDataObserver = false
    }
}