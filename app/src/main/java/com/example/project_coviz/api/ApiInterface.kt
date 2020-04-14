package com.example.project_coviz.api

import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("track")
    fun getLocationAndTimestamps(@Query("cell_token") cell_token: String) : Call<LocationAndTimestampData>

    @GET("track")
    fun getLocationAndTimestampsForHours(@Query("cell_token") cell_token: String, @Query("hours") hours: Int = 72
    ) : Call<LocationAndTimestampData>

    @POST("track")
    fun postLocationAndTimestamps(@Body data: LocationAndTimestampData) : Call<String>

}