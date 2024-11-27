package com.ibs.spato.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.databinding.TrackOrderRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.my_orders_responses.track_order.Track

class TrackOrderAdapter(private val context: Context, private val cartList: ArrayList<Track>)
        : RecyclerView.Adapter<TrackOrderAdapter.TrackOrderViewHolder>() {

    inner class TrackOrderViewHolder(binding : TrackOrderRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        private val status = binding.status
        private val date = binding.date
        private val topView = binding.topView
        private val bottomView = binding.bottomView

        @SuppressLint("SetTextI18n")
        fun bind(track: Track, position: Int) {
            if (position == 0){
                topView.visibility = View.GONE
            }
            if (position == cartList.lastIndex){
                bottomView.visibility = View.GONE
            }
            if (position != 0 && position != cartList.lastIndex){
                topView.visibility = View.VISIBLE
                bottomView.visibility = View.VISIBLE
            }
            status.text = track.label
            date.text = track.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackOrderViewHolder {
        val binding = TrackOrderRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return TrackOrderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: TrackOrderViewHolder, position: Int) {
        holder.bind(cartList[position], position)
    }
}