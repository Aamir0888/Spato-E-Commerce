package com.ibs.spato.utilities

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.ibs.spato.R
import javax.inject.Inject

class SpatoProgressBar @Inject constructor(private val context: Context?) {
    private var isLoading = true
    private val alertDialog = context?.let {
        AlertDialog.Builder(it, R.style.loadingDialog).create()
    }

    fun start() {
        isLoading = true
        val dialogView = LayoutInflater.from(context).inflate(R.layout.spato_progress_bar_layout, null)
        alertDialog!!.setView(dialogView)
        alertDialog.setCancelable(false)
        Handler().postDelayed({
            if (isLoading) {
                alertDialog.show()
            }
        }, Constants.LOADER_TIME)
    }

    fun stop() {
        isLoading = false
        alertDialog!!.dismiss()
    }
}