package com.ibs.spato.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.R
import com.ibs.spato.databinding.AddAddressStateRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.my_addresses.get_region_id.GetRegionIdResponse

class AddAddressStateListAdapter(private val context: Context, private val stateList:
                    ArrayList<GetRegionIdResponse.Data.Region>, private val stateItemClickListener: StateItemClickListener)
        : RecyclerView.Adapter<AddAddressStateListAdapter.MyStateViewHolder>() {

    inner class MyStateViewHolder(binding : AddAddressStateRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        private val stateName = binding.stateName
        private val singleItem = binding.singleItem

        @SuppressLint("SetTextI18n")
        fun bind(regions: GetRegionIdResponse.Data.Region, stateList: ArrayList<GetRegionIdResponse.Data.Region>) {
            stateName.text = regions.label

            singleItem.setOnClickListener {
                for (i in 0 until stateList.size){
                    stateList[i].selectedOrNot = false
                }
                stateItemClickListener.stateClicked(regions)
                singleItem.setBackgroundResource(R.drawable.state_recycler_item_selected_background)
                regions.selectedOrNot = true
            }

            if (regions.selectedOrNot){
                singleItem.setBackgroundResource(R.drawable.state_recycler_item_selected_background)
            } else{
                singleItem.setBackgroundResource(R.color.transparent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStateViewHolder {
        val binding = AddAddressStateRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyStateViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return stateList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: MyStateViewHolder, position: Int) {
        holder.bind(stateList[position], stateList)
    }

    interface StateItemClickListener{
        fun stateClicked(regions: GetRegionIdResponse.Data.Region)
    }
}