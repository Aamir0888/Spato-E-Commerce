package com.ibs.spato.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.databinding.SparePartsRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.dashboard_product_list.Products
import com.ibs.spato.utilities.Utils

class SparePartsListAdapter(
    private val context: Context,
    private val sparePartsList: ArrayList<Products>,
    private val spareItemsClickListener: SpareItemsClickListener
) :
    RecyclerView.Adapter<SparePartsListAdapter.SparePartViewHolder>() {

    inner class SparePartViewHolder(binding: SparePartsRecyclerSingleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val productImage = binding.productImage
        private val productName = binding.productName
        private val productPrice = binding.productPrice
        private val llSingleItem = binding.llSingleItem

        fun bind(products: Products) {
            Utils.loadImage(context, products.image[0], productImage)
            productName.text = products.name
            productPrice.text = products.price.toString()

            llSingleItem.setOnClickListener {
                spareItemsClickListener.onClick(products.product_id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SparePartViewHolder {
        val binding = SparePartsRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return SparePartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return sparePartsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: SparePartViewHolder, position: Int) {
        holder.bind(sparePartsList[position])
    }

    interface SpareItemsClickListener{
        fun onClick(productId: Long)
    }
}