package com.ibs.spato.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ibs.spato.R
import com.ibs.spato.adapters.ViewPagerAdapter
import com.ibs.spato.databinding.ActivityMyOrdersBinding

class MyOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyOrdersBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val ordersArray = arrayOf("Current", "Completed", "Cancelled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = ordersArray[position]
        }.attach()

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}