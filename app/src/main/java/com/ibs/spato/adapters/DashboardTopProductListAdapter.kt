package com.ibs.spato.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.databinding.TopProductListRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.dashboard_product_list.Products
import com.ibs.spato.utilities.Utils

class DashboardTopProductListAdapter(private val context: Context, private val productList:
            ArrayList<Products>, private val itemClickListener: ItemClickListener) :
            RecyclerView.Adapter<DashboardTopProductListAdapter.TopProductListViewHolder>(){

    inner class TopProductListViewHolder(binding: TopProductListRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private val productImage = binding.productImage
        private val productName = binding.productName
        private val oldPrice = binding.oldPrice
        private val newPrice = binding.newPrice
        private val llSingleItem = binding.llSingleItem

        fun bind(topProduct: Products) {
            Utils.loadImage(context, topProduct.image[0], productImage)
            productName.text = topProduct.name
            oldPrice.text = topProduct.old_price.toString()
            newPrice.text = topProduct.price.toString()

            llSingleItem.setOnClickListener {
                itemClickListener.topItemClicked(topProduct.product_id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductListViewHolder {
        val binding = TopProductListRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return TopProductListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: TopProductListViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    interface ItemClickListener{
        fun topItemClicked(productId: Long)
    }
}