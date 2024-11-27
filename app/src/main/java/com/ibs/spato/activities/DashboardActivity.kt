package com.ibs.spato.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import com.ibs.spato.R
import com.ibs.spato.databinding.ActivityDashboardBinding
import com.ibs.spato.databinding.SidebarLayoutBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.fragments.CartsFragment
import com.ibs.spato.fragments.HomeFragment
import com.ibs.spato.fragments.ProductsFragment
import com.ibs.spato.fragments.ProfileFragment
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.service.LoginService
import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var s: String = ""
    @Inject
    lateinit var session: Session
    private lateinit var serviceIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectDashboard(this)
        serviceIntent = Intent(this, LoginService::class.java)
        if (session.getString(Constants.LOGIN_TOKEN) == Constants.NO_DATA){
            startService(serviceIntent)
        } else{
            stopService(serviceIntent)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if (!it.isSuccessful) {
//                Log.e("FirebaseDeviceToken","Token Failed to Receive")
                return@OnCompleteListener
            }
            mainViewModel.saveFcmToken(it.result)
//            Log.d("FirebaseDeviceToken",it.result.toString())
        })

        sideBarClicks()
        bottomBarClicks()

        binding.notifications.setOnClickListener {
            if (session.getString(Constants.LOGIN_TOKEN) == Constants.NO_DATA){
                startActivity(Intent(this, LoginActivity::class.java))
            } else{
                startActivity(Intent(this, NotificationsActivity::class.java))
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        if (session.getString(Constants.LOGIN_TOKEN) == Constants.NO_DATA){
//            startService(serviceIntent)
//        } else{
//            stopService(serviceIntent)
//        }
//    }

    private fun bottomBarClicks() {
        addFragment(HomeFragment())
        binding.homeLinearLayout.setOnClickListener {
            replaceHomeFragment(HomeFragment())
            buttonClicked(binding.homeLinearLayout)
        }
        binding.productLinearLayout.setOnClickListener {
            replaceFragment(ProductsFragment())
            buttonClicked(binding.productLinearLayout)
        }
        binding.cartsLinearLayout.setOnClickListener {
            if (session.getString(Constants.LOGIN_TOKEN) == Constants.NO_DATA){
                startActivity(Intent(this, LoginActivity::class.java))
            } else{
                replaceFragment(CartsFragment())
                buttonClicked(binding.cartsLinearLayout)
            }
        }
        binding.profileLinearLayout.setOnClickListener {
            if (session.getString(Constants.LOGIN_TOKEN) == Constants.NO_DATA){
                startActivity(Intent(this, LoginActivity::class.java))
            } else{
                replaceFragment(ProfileFragment())
                buttonClicked(binding.profileLinearLayout)
            }
        }
    }

    private fun sideBarClicks() {
        val sideBarLayoutBinding: SidebarLayoutBinding = binding.navigationView
        val crossImage: ImageView = sideBarLayoutBinding.sidebarCross
        val llHome: LinearLayout = sideBarLayoutBinding.llHome
        val llMyOrders: LinearLayout = sideBarLayoutBinding.llMyOrders
        val llSettings: LinearLayout = sideBarLayoutBinding.llSettings
        val llCompanyPolicy: LinearLayout = sideBarLayoutBinding.llCompanyPolicy
        val llTermsAndConditions: LinearLayout = sideBarLayoutBinding.llTermsAndConditions
        val llNewsletters: LinearLayout = sideBarLayoutBinding.llNewsletters
        val llHelp: LinearLayout = sideBarLayoutBinding.llHelp
        val llLogout: LinearLayout = sideBarLayoutBinding.llLogout
        val logoutImage: ImageView = sideBarLayoutBinding.logoutImage
        val logoutText: TextView = sideBarLayoutBinding.logoutText

        if (session.getString(Constants.LOGIN_TOKEN) == Constants.NO_DATA){
            logoutImage.rotation = 180F
            logoutText.text = getString(R.string.login2)
            llMyOrders.visibility = View.GONE
        } else{
            logoutImage.rotation = 0F
            logoutText.text = getString(R.string.logout)
            llMyOrders.visibility = View.VISIBLE
        }

        binding.hamburger.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        crossImage.setOnClickListener {
            closeDrawer()
        }

        llHome.setOnClickListener {
            closeDrawer()
            replaceHomeFragment(HomeFragment())
            buttonClicked(binding.homeLinearLayout)
        }

        llMyOrders.setOnClickListener {
            closeDrawer()
            openActivity(MyOrdersActivity())
        }

        llSettings.setOnClickListener {
            closeDrawer()
            openActivity(SettingsActivity())
        }

        llCompanyPolicy.setOnClickListener {
            closeDrawer()
            openActivity(CompanyPolicyActivity())
        }

        llTermsAndConditions.setOnClickListener {
            closeDrawer()
            openActivity(TermsConditionsActivity())
        }

        llNewsletters.setOnClickListener {
            closeDrawer()
            openActivity(NewsletterActivity())
        }

        llHelp.setOnClickListener {
            closeDrawer()
            openActivity(HelpActivity())
        }

        llLogout.setOnClickListener {
            if (session.getString(Constants.LOGIN_TOKEN) == Constants.NO_DATA){
                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
            } else{
                logoutClicked()
            }
        }
    }

    private fun logoutClicked() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.logout_bottom_sheet_layout, null)
        val cancel: TextView = view.findViewById(R.id.cancel)
        val logout: LinearLayout = view.findViewById(R.id.llBottomSheetLogout)

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        logout.setOnClickListener {
            logoutApi()
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun openActivity(activity: Activity) {
        GlobalScope.launch(Dispatchers.Main) {
            delay(200)
            startActivity(Intent(this@DashboardActivity, activity::class.java))
        }
    }

    private fun logoutApi() {
        mainViewModel.logout()
        observe()
    }

    private fun observe() {
        mainViewModel.logoutResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra(Constants.KEY, Constants.VALUE)
                    startActivity(intent)
                    session.logOut()
                    finish()
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.dashboardFragmentContainer, fragment).commit()
    }
    private fun replaceFragment(fragment: Fragment) {
        s = fragment.toString()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashboardFragmentContainer, fragment).commit()
    }
    private fun replaceHomeFragment(fragment: Fragment) {
        s = fragment.toString()
        val manager: FragmentManager = supportFragmentManager
        val trans = manager.beginTransaction()
        trans.remove(fragment)
        trans.replace(R.id.dashboardFragmentContainer, fragment)
        trans.commit()
        manager.popBackStack()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else if (s.startsWith("ProductsFragment")) {
            replaceFragment(HomeFragment())
            buttonClicked(binding.homeLinearLayout)
        }
        else if (s.startsWith("CartsFragment")) {
            replaceFragment(HomeFragment())
            buttonClicked(binding.homeLinearLayout)
        }
        else if (s.startsWith("ProfileFragment")) {
            replaceFragment(HomeFragment())
            buttonClicked(binding.homeLinearLayout)
        } else {
//            super.onBackPressed()
            finish()
        }
    }

    private fun buttonClicked(layout: LinearLayout) {
        binding.llHome.setBackgroundResource(R.color.transparent)
        binding.llProducts.setBackgroundResource(R.color.transparent)
        binding.llCarts.setBackgroundResource(R.color.transparent)
        binding.llProfile.setBackgroundResource(R.color.transparent)
        binding.dashHomeIcon.setImageResource(R.drawable.dash_home)
        binding.dashProductIcon.setImageResource(R.drawable.product_unselected)
        binding.dashCartsIcon.setImageResource(R.drawable.cart_unselected)
        binding.dashProfileIcon.setImageResource(R.drawable.dash_profile)
        binding.tvHome.setTextColor(resources.getColor(R.color.half_white))
        binding.tvProduct.setTextColor(resources.getColor(R.color.half_white))
        binding.tvCarts.setTextColor(resources.getColor(R.color.half_white))
        binding.tvProfile.setTextColor(resources.getColor(R.color.half_white))
        when (layout) {
            binding.homeLinearLayout -> {
                binding.llHome.setBackgroundResource(R.drawable.bottom_nav_clicked_item_background)
                binding.dashHomeIcon.setImageResource(R.drawable.dash_home_black)
                binding.tvHome.setTextColor(resources.getColor(R.color.spato_primary_color))
            }
            binding.productLinearLayout -> {
                binding.llProducts.setBackgroundResource(R.drawable.bottom_nav_clicked_item_background)
                binding.dashProductIcon.setImageResource(R.drawable.product_selected)
                binding.tvProduct.setTextColor(resources.getColor(R.color.spato_primary_color))
            }
            binding.cartsLinearLayout -> {
                binding.llCarts.setBackgroundResource(R.drawable.bottom_nav_clicked_item_background)
                binding.dashCartsIcon.setImageResource(R.drawable.cart_selected)
                binding.tvCarts.setTextColor(resources.getColor(R.color.spato_primary_color))
            }
            binding.profileLinearLayout -> {
                binding.llProfile.setBackgroundResource(R.drawable.bottom_nav_clicked_item_background)
                binding.dashProfileIcon.setImageResource(R.drawable.dash_profile_black)
                binding.tvProfile.setTextColor(resources.getColor(R.color.spato_primary_color))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceIntent)
    }
}