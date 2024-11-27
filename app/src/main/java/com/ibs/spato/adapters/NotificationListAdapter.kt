package com.ibs.spato.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.databinding.NotificationsRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.notification_list.NotificationListResponse

class NotificationListAdapter(private val context: Context, private val notificationList: ArrayList<NotificationListResponse.Data>):
        RecyclerView.Adapter<NotificationListAdapter.NotificationsViewHolder>(){

    inner class NotificationsViewHolder(binding: NotificationsRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private val notificationTitle = binding.notificationTitle
        private val notificationBody = binding.notificationBody
        private val notificationTime = binding.notificationTime

        fun bind(data: NotificationListResponse.Data) {
            notificationTitle.text = data.title
            notificationBody.text = data.message
            notificationTime.text = data.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val binding = NotificationsRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }
}