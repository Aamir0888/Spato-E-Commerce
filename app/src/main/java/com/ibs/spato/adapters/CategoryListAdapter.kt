package com.ibs.spato.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.R
import com.ibs.spato.databinding.DashboardCategoryListRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.dashboard_category_list.CategoryListResponse
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.Utils

class CategoryListAdapter(
    private val context: Context,
    private val categoryList: ArrayList<CategoryListResponse.Data.Category>,
    private val moreClickListener: MoreClickListener,
) : RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>(){

    inner class CategoryListViewHolder(binding: DashboardCategoryListRecyclerSingleItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(category: CategoryListResponse.Data.Category) {
            llCategory.visibility = View.VISIBLE
            categoryName.visibility = View.VISIBLE
            Utils.loadImage(context, category.image, categoryImage)
            categoryName.text = category.name

            llSingleItem.setOnClickListener {
                moreClickListener.categoryItemClicked(category)
            }
        }

        private val categoryImage = binding.categoryImage
        private val categoryName = binding.categoryName
        private val llCategory = binding.llCategory
        private val llSingleItem = binding.llSingleItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding = DashboardCategoryListRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return CategoryListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    interface MoreClickListener{
        fun categoryItemClicked(category: CategoryListResponse.Data.Category)
    }
}