package com.ibs.spato.adapters

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.databinding.NewsletterRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.newsletter_list.NewsletterListResponse

class NewslettersAdapter(private val context: Context, private val newsletterList: ArrayList<NewsletterListResponse.Data>):
        RecyclerView.Adapter<NewslettersAdapter.NewslettersViewHolder>(){

    inner class NewslettersViewHolder(binding: NewsletterRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private val newsletterTitle = binding.newsletterTitle
        private val newsletterBody = binding.newsletterBody
        private val newsletterTime = binding.newsletterTime

        fun bind(data: NewsletterListResponse.Data) {
            newsletterTitle.text = data.title
            newsletterTime.text = data.date
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val text = Html.fromHtml(data.message, Html.FROM_HTML_MODE_LEGACY)
                val text1 = text.split("\n").toTypedArray()
                newsletterBody.text = text1[0]
            } else {
                val text = Html.fromHtml(data.message)
                val text1 = text.split("\n").toTypedArray()
                newsletterBody.text = text1[0]
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewslettersViewHolder {
        val binding = NewsletterRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return NewslettersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsletterList.size
    }

    override fun onBindViewHolder(holder: NewslettersViewHolder, position: Int) {
        holder.bind(newsletterList[position])
    }
}