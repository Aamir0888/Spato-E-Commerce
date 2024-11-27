package com.ibs.spato.di

import android.content.Context
import com.ibs.spato.retrofit.AuthInterceptor
import com.ibs.spato.retrofit.InternetConnectivityInterceptor
import com.ibs.spato.retrofit.SpatoAPI
import com.ibs.spato.session.Session
import com.ibs.spato.utilities.Constants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Module
class NetworkModule @Inject constructor(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext(): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideSession() : Session{
        return Session(context)
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(session: Session) : AuthInterceptor{
        return AuthInterceptor(session)
    }

    @Singleton
    @Provides
    fun provideInternetConnectivityInterceptor() : InternetConnectivityInterceptor{
        return InternetConnectivityInterceptor(context)
    }

    @Singleton
    @Provides
    fun providesRetrofit(authInterceptor: AuthInterceptor, internetInterceptor: InternetConnectivityInterceptor) : Retrofit{
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Set the connection timeout to 30 seconds
            .readTimeout(30, TimeUnit.SECONDS) // Set the read timeout to 30 seconds
            .writeTimeout(30, TimeUnit.SECONDS) // Set the write timeout to 30 seconds
            .addInterceptor(internetInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    @Singleton
    @Provides
    fun providesSpatoAPI(retrofit: Retrofit) : SpatoAPI{
        return retrofit.create(SpatoAPI::class.java)
    }
}