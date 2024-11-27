package com.ibs.spato.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.R
import com.ibs.spato.databinding.UserGuidePdfRecyclerSingleItemLayoutBinding

class UserGuidePdfAdapter(private val context: Context, private val pdfList: ArrayList<String>, private val clickListener: UserGuideClickListener):
        RecyclerView.Adapter<UserGuidePdfAdapter.UserGuideViewHolder>(){

    inner class UserGuideViewHolder(binding: UserGuidePdfRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private val text = binding.text
        private val singleItem = binding.singleItem

        @SuppressLint("SetTextI18n")
        fun bind(data: String, position: Int) {
            text.text = "${context.getText(R.string.user_guide)} ${position+1}"

            singleItem.setOnClickListener {
                clickListener.userGuideClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserGuideViewHolder {
        val binding = UserGuidePdfRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserGuideViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pdfList.size
    }

    override fun onBindViewHolder(holder: UserGuideViewHolder, position: Int) {
        holder.bind(pdfList[position], position)
    }

    interface UserGuideClickListener{
        fun userGuideClick(pdf: String)
    }
}