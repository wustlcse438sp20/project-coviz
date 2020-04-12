package com.example.project_coviz.api

import androidx.lifecycle.MutableLiveData


class ApiRepository(val service: ApiInterface) {

    val locationAndTimestamps: MutableLiveData<LocationAndTimestampData> = MutableLiveData()

    fun updateTimestampsForCurrentLocation(cell_token: String) {
        //Need to prepend appropriate search parameter to the query e.g. prepend the "artist:"
        // to	https://api.deezer.com/search?q=artist:"aloe blacc"
        getLocationAndTimestamps(locationAndTimestamps, cell_token)
    }

    fun getLocationAndTimestamps(
        resBody: MutableLiveData<LocationAndTimestampData>, cell_token: String) {
        //Need to prepend appropriate search parameter to the query e.g. prepend the "artist:"
        // to	https://api.deezer.com/search?q=artist:"aloe blacc"
        ApiClient.runQuery(service::getLocationAndTimestamps, resBody, cell_token)
    }

    fun postLocationAndTimestamps(data: LocationAndTimestampData) {
        // throw result away
        ApiClient.runQuery(service::postLocationAndTimestamps, MutableLiveData(), data)
    }

}

