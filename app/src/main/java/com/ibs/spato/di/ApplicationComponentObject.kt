package com.ibs.spato.di

import android.content.Context

object ApplicationComponentObject {
    fun create(context: Context): ApplicationComponent {
        return DaggerApplicationComponent.builder().networkModule(NetworkModule(context)).build()
    }
}