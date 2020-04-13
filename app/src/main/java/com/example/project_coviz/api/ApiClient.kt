package com.example.project_coviz.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.Key
import com.natpryce.konfig.stringType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

object ApiClient {
    val base_url = "http://142.93.253.235"
    val APIRepository = ApiRepository(ApiClient.makeRetrofitService())

    fun makeRetrofitService(): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(ApiInterface::class.java)

    }

    fun <U,T> runQuery(f: (U) -> Call<T>, liveData: MutableLiveData<T>, query: U) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = f(query).execute()
            if(result.isSuccessful) {
                println("success " /* + result.body() */ )
                withContext(Dispatchers.Main) {
                    liveData.setValue(result.body())
                }
            }
            else {
                Log.d("NETWORK", "FAILED")
            }
        }
    }

    fun <U,T> runQuery(f: (U) -> Call<T>, callback: (T?) -> Any, query: U) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = f(query).execute()
            if(result.isSuccessful) {
                println("success " /* + result.body() */ )
                withContext(Dispatchers.Main) {
                    callback(result.body())
                }
            }
            else {
                Log.d("NETWORK", "FAILED")
            }
        }
    }

    fun <U, T> runQuery(f: () -> Call<T>, liveData: MutableLiveData<T>) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = f().execute()
            if(result.isSuccessful) {
                println("success " /* + result.body() */ )
                withContext(Dispatchers.Main) {
                    liveData.setValue(result.body())
                }
            }
            else {
                Log.d("NETWORK", "FAILED")
            }
        }
    }

}