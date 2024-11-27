package com.ibs.spato.retrofit

import android.content.Context
import com.ibs.spato.R
import com.ibs.spato.utilities.InternetConnection
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class InternetConnectivityInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!InternetConnection.checkConnection(context)) {
            throw NoConnectivityException(context.getString(R.string.no_internet_connection))
        } else{
            chain.proceed(chain.request())
        }
    }
}
class NoConnectivityException(message: String) : IOException(message)
