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

class CategoryListAdapterMoreLess(
    private val context: Context,
    private val categoryList: ArrayList<CategoryListResponse.Data.Category>,
    private val moreClickListener: MoreClickListener,
    private val lessMore: String
) : RecyclerView.Adapter<CategoryListAdapterMoreLess.CategoryListViewHolder>(){

    inner class CategoryListViewHolder(binding: DashboardCategoryListRecyclerSingleItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(category: CategoryListResponse.Data.Category, position: Int) {

//            llCategory.visibility = View.VISIBLE
//            categoryName.visibility = View.VISIBLE
//            Utils.loadImage(context, category.image, categoryImage)
//            categoryName.text = category.name
//
//            llSingleItem.setOnClickListener {
//                moreClickListener.categoryItemClicked(category)
//            }

            if (lessMore == Constants.CATEGORY_LESS){
                if (position == 4){
                    llCategoryMore.visibility = View.VISIBLE
                    moreText.visibility = View.VISIBLE
                    llCategory.visibility = View.GONE
                    categoryName.visibility = View.GONE

                    llCategoryMore.setOnClickListener {
                        moreClickListener.moreClicked()
                    }
                } else{
                    llCategoryMore.visibility = View.GONE
                    moreText.visibility = View.GONE
                    llCategory.visibility = View.VISIBLE
                    categoryName.visibility = View.VISIBLE
                    Utils.loadImage(context, category.image, categoryImage)
                    categoryName.text = category.name

                    llSingleItem.setOnClickListener {
                        moreClickListener.categoryItemClicked(category)
                    }
                }
            }
            else{
                if (position == categoryList.lastIndex){
                    llCategoryMore.visibility = View.VISIBLE
                    moreText.visibility = View.VISIBLE
                    llCategory.visibility = View.GONE
                    categoryName.visibility = View.GONE
                    moreText.text = context.getString(R.string.less)

                    llCategoryMore.setOnClickListener {
                        moreClickListener.lessClicked()
                    }
                } else{
                    llCategoryMore.visibility = View.GONE
                    moreText.visibility = View.GONE
                    llCategory.visibility = View.VISIBLE
                    categoryName.visibility = View.VISIBLE
                    Utils.loadImage(context, category.image, categoryImage)
                    categoryName.text = category.name

                    llSingleItem.setOnClickListener {
                        moreClickListener.categoryItemClicked(category)
                    }
                }
            }
        }

        private val categoryImage = binding.categoryImage
        private val categoryName = binding.categoryName
        private val llCategoryMore = binding.llCategoryMore
        private val llCategory = binding.llCategory
        private val moreText = binding.moreText
        private val llSingleItem = binding.llSingleItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding = DashboardCategoryListRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return CategoryListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (lessMore == Constants.CATEGORY_MORE){
            categoryList.size
        } else {
            if (categoryList.size > 4){
                5
            } else{
                categoryList.size
            }
        }
//        return categoryList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        holder.bind(categoryList[position], position)
    }

    interface MoreClickListener{
        fun moreClicked()
        fun lessClicked()
        fun categoryItemClicked(category: CategoryListResponse.Data.Category)
    }
}