package com.ibs.spato.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.R
import com.ibs.spato.activities.EditProfileActivity
import com.ibs.spato.activities.MyAddressesActivity
import com.ibs.spato.databinding.FragmentProfileBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.customer_details.CustomerDetailsData
import com.ibs.spato.responses.product_details.Product
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private lateinit var alertDialog: AlertDialog
    private lateinit var alertView: View
    private lateinit var llYes: LinearLayout
    private lateinit var llNo: LinearLayout
    @Inject
    lateinit var session: Session
    private lateinit var customerData: CustomerDetailsData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        ApplicationComponentObject.create(requireContext()).injectProfileFragment(this)
        apiCall()
        observe()
        dialog()
        onClicks()

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCall()
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        mainViewModel.customerDetailsResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    if (isAdded){
                        customerData = it.data!!.data
                        binding.userName.text = "${customerData.first_name} ${customerData.last_name}"
                        binding.userEmail.text = customerData.email
                        Utils.loadImage(requireContext(), it.data.data.profile_pic, binding.profilePic, R.drawable.profile_pic)
                        stopProgressBar(Constants.SUCCESS)
                    }
                }

                is NetworkResult.Error -> {
                    stopProgressBar(Constants.FAILURE)
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    stopProgressBar(Constants.FAILURE)
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        mainViewModel.commonResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    session.logOut()
                    requireActivity().finishAffinity()
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun apiCall() {
        mainViewModel.customerDetails()
    }

    private fun onClicks() {
        binding.llBasicInformation.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(Constants.CUSTOMER_DATA, customerData)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.llMyAddresses.setOnClickListener {
            startActivity(Intent(activity, MyAddressesActivity::class.java))
        }
        binding.topView.setOnClickListener {
            //  do nothing
        }
        binding.deleteAccount.setOnClickListener {
            alertDialog.setView(alertView)
            alertDialog.show()
        }
    }

    private fun stopProgressBar(status: String) {
        spatoProgressBar.stop()
        binding.pullToRefresh.isRefreshing = false
        if (status == Constants.FAILURE){
            binding.topView.visibility = View.VISIBLE
            binding.noData.visibility = View.VISIBLE
        } else if (status == Constants.SUCCESS){
            binding.topView.visibility = View.INVISIBLE
            binding.noData.visibility = View.INVISIBLE
        }
    }

    private fun dialog() {
        alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog).create()
        alertView = LayoutInflater.from(requireContext()).inflate(R.layout.delete_confirmation_dialog, null)
        llYes = alertView.findViewById(R.id.llYes)
        llNo = alertView.findViewById(R.id.llNo)

        llNo.setOnClickListener {
            alertDialog.dismiss()
        }
        llYes.setOnClickListener {
            alertDialog.dismiss()
            mainViewModel.deleteAccount()
            loading = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (EditProfileActivity.profileEdited){
            apiCall()
        }
    }

    override fun onDetach() {
        super.onDetach()
        spatoProgressBar.stop()
    }
}