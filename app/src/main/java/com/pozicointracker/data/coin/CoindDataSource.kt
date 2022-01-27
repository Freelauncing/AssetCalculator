package com.pozicointracker.data.coin

import androidx.lifecycle.LiveData
import com.pozicointracker.data.Result
import com.pozicointracker.data.coin.model.Coin

interface CoinDataSource {

    fun observeCoins(): LiveData<Result<List<Coin>>>

    suspend fun getCoins(): Result<List<Coin>>

    suspend fun refreshCoins()

    fun observeCoin(CoinId: String): LiveData<Result<Coin>>

    suspend fun getCoin(CoinId: String): Result<Coin>

    suspend fun refreshCoin(CoinId: String)

    suspend fun saveCoin(coin: Coin)

    suspend fun deleteAllCoins()

    suspend fun deleteCoin(CoinId: String)
}