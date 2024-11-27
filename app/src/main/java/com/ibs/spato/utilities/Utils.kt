package com.ibs.spato.utilities

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.DownloadManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ibs.spato.R
import com.ibs.spato.service.LoginService
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.io.ByteArrayOutputStream
import java.util.Locale

object Utils {
    fun checkPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else{
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), Constants.STORAGE_PERMISSION_REQUEST_CODE)
        } else{
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.STORAGE_PERMISSION_REQUEST_CODE)
        }
    }

    fun permissionCheck(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Constants.STORAGE_PERMISSION_REQUEST_CODE)
    }

    fun bitmapToBase64(bitmap: Bitmap) : String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun loadImage(context: Context, url: String, view: ImageView){
        Glide.with(context).load(url)
            .apply(RequestOptions.placeholderOf(R.drawable.product_dummy)
                .error(R.drawable.product_dummy)).into(view)
    }

    fun loadImage(context: Context, url: String, view: ImageView, dummyImage: Int){
        Glide.with(context).load(url)
            .apply(RequestOptions.placeholderOf(dummyImage)
                .error(dummyImage)).into(view)
    }

    private val mutablePosition = MutableLiveData<Int>()
    val position: LiveData<Int> get() = mutablePosition
    fun swipeToDelete(context: Context, recyclerView: RecyclerView, deleteImage: Int, bgColor: Int){
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                // this method is called
                // when the item is moved.
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                mutablePosition.postValue(viewHolder.adapterPosition)
            }
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                RecyclerViewSwipeDecorator.Builder(context, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, bgColor))
                    .addSwipeLeftActionIcon(deleteImage)
                    .addCornerRadius(1,15)
//                    .addBackgroundColor(ContextCompat.getColor(this@NotificationActivity, R.color.black))
//                    .addActionIcon(R.drawable.email)
                    .create()
                    .decorate()
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        // at last we are adding this
        // to our recycler view.
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun expand(v: View) {
        if (v.visibility == View.VISIBLE) return
        val durations: Long
        val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            (v.parent as View).width,
            View.MeasureSpec.EXACTLY
        )
        val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        )
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        durations = ((targetHeight / v.context.resources
            .displayMetrics.density)).toLong()

        v.alpha = 0.0F
        v.visibility = View.VISIBLE
        v.animate().alpha(1.0F).setDuration(durations).setListener(null)

        val a: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = durations
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        if (v.visibility == View.GONE) return
        val durations: Long
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        durations = (initialHeight / v.context.resources
            .displayMetrics.density).toLong()

        v.alpha = 1.0F
        v.animate().alpha(0.0F).setDuration(durations)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    v.visibility = View.GONE
                    v.alpha = 1.0F
                }
            })

        // Collapse speed of 1dp/ms
        a.duration = durations
        v.startAnimation(a)
    }

    fun setAppLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun downloadPdf(url: String, context: Context) {
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, null, null))
        val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        request.setMimeType("application/pdf")
        request.allowScanningByMediaScanner()
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        downloadManager.enqueue(request)
        Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_SHORT).show()
    }
}