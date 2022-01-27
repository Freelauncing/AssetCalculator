package com.pozicointracker.data.coin

import androidx.lifecycle.LiveData
import com.pozicointracker.data.Result
import com.pozicointracker.data.coin.model.Coin

interface CoinRepository {

    fun observeCoins(): LiveData<Result<List<Coin>>>

    suspend fun getCoins(forceUpdate: Boolean = false): Result<List<Coin>>

    suspend fun refreshCoins()

    suspend fun refreshCoin(coinId: String)

    fun observeCoin(coinId: String): LiveData<Result<Coin>>

    suspend fun getCoin(coinId: String, forceUpdate: Boolean = false): Result<Coin>

    suspend fun saveCoin(product: Coin)

    suspend fun deleteAllCoins()

    suspend fun deleteCoin(coinId: String)

}