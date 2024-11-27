package com.ibs.spato.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.databinding.MyOrdersRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.my_orders_responses.all_orders.MyOrdersData
import com.ibs.spato.utilities.Utils

class MyOrdersAdapter(private val context: Context, private val ordersList: ArrayList<MyOrdersData>,
                      private val clickListener : ClickListener):
        RecyclerView.Adapter<MyOrdersAdapter.OrdersViewHolder>(){

    inner class OrdersViewHolder(binding: MyOrdersRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private val productImage = binding.productPic
//        private val tvArticleNo = binding.tvArticleNo
        private val productName = binding.productName
        private val productPrice = binding.productPrice
        private val dateTime = binding.dateTime
        private val llSingleItem = binding.llSingleItem
//        private val paymentMethod = binding.paymentMethod

        fun bind(currentOrdersData: MyOrdersData){
            val currentOrdersProduct = currentOrdersData.product[0]
            Utils.loadImage(context, currentOrdersProduct.image, productImage)
            productName.text = currentOrdersProduct.name
            productPrice.text = currentOrdersProduct.price.toString()
            dateTime.text = currentOrdersData.updated_date

            llSingleItem.setOnClickListener {
                clickListener.onClick(currentOrdersData.order_id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val binding = MyOrdersRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return OrdersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(ordersList[position])
    }

    interface ClickListener{
        fun onClick(orderId: Long)
    }
}