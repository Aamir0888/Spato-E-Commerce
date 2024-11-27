package com.ibs.spato.retrofit

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ibs.spato.R
import org.json.JSONObject
import retrofit2.Response

//fun <T> safeApiCall(call: suspend ()->Response<CommonResponse2>, response: MutableLiveData<NetworkResult<CommonResponse2>>, context: Context):Flow<NetworkResult<T?>> = flow {
//    emit(NetworkResult.Loading())
//    try {
//        val c = call.invoke()
//        if (c.isSuccessful && c.body() != null && c.code() == 200) {
//            if (c.body()!!.status == 200) {
//                response.postValue(NetworkResult.Success(c.body()))
//            } else {
//                response.postValue(NetworkResult.Error(data = c.body()!!, message = null))
//            }
//        } else {
//            response.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
//        }
//    } catch (e: NoConnectivityException) {
//        response.postValue(NetworkResult.NoInternet(e.message))
//    } catch (e: Exception) {
//        response.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
//    }
//}

suspend fun <T> safeApiCall(context: Context, response: MutableLiveData<NetworkResult<T>>, apiCall: suspend () -> Response<T>) {
    response.postValue(NetworkResult.Loading())
    try {
        val c = apiCall()
        if (c.isSuccessful && c.body() != null && c.code() == 200) {
//            Log.d("aamir", "xyz")
//            val json = JSONObject(c.body().toString()) // String instance holding the above json
//
//            val jsonArray: JSONObject = c.(0)
//
//            val keys: Iterator<*> = jsonArray.keys()
//
//            while (keys.hasNext()) {
//                val key = keys.next() as String
//                println("Key: $key")
//                println("Value: " + jsonArray[key])
//            }
//
//            val responseBodyString = c.body().string()
//            if (responseBodyString != null) {
//                val jsonObject = JSONObject(responseBodyString)
//                val desiredValue = jsonObject.optString("key1", "")
//                // You can replace "key1" with the key you want to retrieve.
//                return NetworkResult.Success(desiredValue as T)
//            }
//
//
//            val status = json.getString("status")
//            val message = json.getString("message")
//            val data = json.getString("data")
//            Log.d("aamir", status.toString())
//            Log.d("aamir", message.toString())
//            Log.d("aamir", data.toString())
//            if (c.body()!!.status == 200) {
//                response.postValue(NetworkResult.Success(c.body()))
//            } else {
//                response.postValue(NetworkResult.Error(data = c.body()!!, message = null))
//            }
//            response.postValue(NetworkResult.Success(c.body()))
        } else {
            response.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    } catch (e: Exception) {
        when (e) {
            is NoConnectivityException -> response.postValue(NetworkResult.NoInternet(e.message))
            else -> response.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }
}
