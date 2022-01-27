package com.pozicointracker.data.coin.remote

import com.pozicointracker.data.coin.model.Coin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import java.util.concurrent.TimeUnit


class CoinRetreiver {

    private val networkInterface: NetworkInterface

    companion object{
        var BASE_URL =  "https://api.coingecko.com/api/v3/coins/"
    }

    init {
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build()

        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        networkInterface = retrofit.create(NetworkInterface::class.java)
    }


    suspend fun getCoinsPlease():List<Coin>{
        return networkInterface.getCoinsFormCoingeko(
            "usd","market_cap_desc",
            100,1,false)
    }
}