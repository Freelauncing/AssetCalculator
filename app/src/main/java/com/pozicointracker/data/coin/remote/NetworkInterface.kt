package com.pozicointracker.data.coin.remote

import com.pozicointracker.data.coin.model.Coin
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkInterface {

    @GET("markets")
    suspend fun getCoinsFormCoingeko(
        @Query("vs_currency") currency:String,
        @Query("order")  order:String,
        @Query("per_page")  per_page:Int,
        @Query("page")  page:Int,
        @Query("sparkline")  sparkline:Boolean,
    ) : List<Coin>
}