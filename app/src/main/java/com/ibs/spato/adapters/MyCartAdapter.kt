package com.ibs.spato.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.databinding.MyCartRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.cart_list.MyCartItems
import com.ibs.spato.utilities.Utils

class MyCartAdapter(private val context: Context, private val cartList:
        ArrayList<MyCartItems>, private val cartItemClickListener: CartItemClickListener)
        : RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {

    inner class MyCartViewHolder(binding : MyCartRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        private val productImage = binding.productPic
        private val productName = binding.productName
        private val productPrice = binding.productPrice
        private val productQuantity = binding.productQuantity
        private val subtractTv = binding.subtractTv
        private val addTv = binding.addTv

        @SuppressLint("SetTextI18n")
        fun bind(myCartItems: MyCartItems) {
            Utils.loadImage(context, myCartItems.image, productImage)
            productName.text = myCartItems.name
            productPrice.text = myCartItems.price
            productQuantity.text = myCartItems.qty.toString()

            subtractTv.setOnClickListener {
                if (myCartItems.qty==1){
                    cartItemClickListener.showMessage()
                } else{
                    cartItemClickListener.subtract(myCartItems)
                }
            }
            addTv.setOnClickListener {
                cartItemClickListener.add(myCartItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        val binding = MyCartRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyCartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    interface CartItemClickListener{
        fun add(myCartItems: MyCartItems)
        fun subtract(myCartItems: MyCartItems)
        fun showMessage()
    }
}