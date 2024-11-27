package com.ibs.spato.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ibs.spato.fragments.CancelledOrdersFragment
import com.ibs.spato.fragments.CompletedOrdersFragment
import com.ibs.spato.fragments.CurrentOrdersFragment

class ViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
//        when (position) {
//            0 -> return CurrentOrdersFragment()
//            1 -> return CompletedOrdersFragment()
//        }
//        return CancelledOrdersFragment()
        when (position) {
            1 -> return CompletedOrdersFragment()
            2 -> return CancelledOrdersFragment()
        }
        return CurrentOrdersFragment()
    }
}