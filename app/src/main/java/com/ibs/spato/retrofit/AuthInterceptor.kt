package com.ibs.spato.retrofit

import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val session: Session) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val token = session.getString(Constants.LOGIN_TOKEN)
        request.addHeader("Authorization", "Bearer $token")
        return chain.proceed(request.build())
    }
}