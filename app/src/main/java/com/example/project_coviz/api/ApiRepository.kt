package com.example.project_coviz.api

import android.util.Log
import androidx.lifecycle.MutableLiveData


class ApiRepository(val service: ApiInterface) {

    val locationAndTimestamps: MutableLiveData<LocationAndTimestampData> = MutableLiveData()

    fun updateTimestampsForCurrentLocation(cell_token: String) {
        getLocationAndTimestamps(locationAndTimestamps, cell_token)
    }

    fun updateTimestampsForCurrentLocation(cell_token: String, hours: Int) {
        getLocationAndTimestamps(locationAndTimestamps, cell_token, hours)
    }

    fun getLocationAndTimestamps(
        resBody: MutableLiveData<LocationAndTimestampData>, cell_token: String) {
        ApiClient.runQuery(service::getLocationAndTimestamps, resBody, cell_token)
    }

    fun getLocationAndTimestamps(
        resBody: MutableLiveData<LocationAndTimestampData>, cell_token: String, hours: Int) {
        ApiClient.runQuery(service::getLocationAndTimestampsForHours, resBody, cell_token, hours)
    }

    fun postLocationAndTimestamps(data: LocationAndTimestampData) {
        // throw result away
        ApiClient.runQuery(service::postLocationAndTimestamps, { Log.d("NETWORK","Logged data ${data}")}, data)
    }

}

