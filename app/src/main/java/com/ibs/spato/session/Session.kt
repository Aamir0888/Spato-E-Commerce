package com.ibs.spato.session

import android.content.Context
import android.content.SharedPreferences
import com.ibs.spato.utilities.Constants
import javax.inject.Inject

class Session @Inject constructor(private val context: Context?){
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun setString(key: String?, value: String?) {
        preferences = context!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = preferences.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getString(key: String) : String{
        preferences = context!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(key, Constants.NO_DATA).toString()
    }

    fun logOut(){
        preferences = context!!.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}