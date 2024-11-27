package com.ibs.spato.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.databinding.DashboardProductListRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.dashboard_product_list.Products
import com.ibs.spato.utilities.Utils

class ProductListAdapter(private val context: Context, private val productList:
    ArrayList<Products>, private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>(){

    inner class ProductListViewHolder(binding: DashboardProductListRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private val productImage = binding.productPic
        private val productName = binding.productName
        private val productPrice = binding.productPrice
        private val llSingleItem = binding.llSingleItem

        fun bind(topProduct: Products) {
            Utils.loadImage(context, topProduct.image[0], productImage)
            productName.text = topProduct.name
            productPrice.text = topProduct.price.toString()

            llSingleItem.setOnClickListener {
                itemClickListener.onClick(topProduct.product_id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val binding = DashboardProductListRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    interface ItemClickListener{
        fun onClick(productId: Long)
    }
}