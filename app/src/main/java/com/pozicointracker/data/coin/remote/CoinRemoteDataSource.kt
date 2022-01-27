package com.pozicointracker.data.coin.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.pozicointracker.data.Result
import com.pozicointracker.data.coin.CoinDataSource
import com.pozicointracker.data.coin.model.Coin
import kotlinx.coroutines.delay

object CoinRemoteDataSource : CoinDataSource {

    private val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var PRODUCTS_SERVICE_DATA = LinkedHashMap<String, Coin>(2)

    val coinRetreiver: CoinRetreiver = CoinRetreiver()

    private val observableCoins = MutableLiveData<Result<List<Coin>>>()

    //    init {
//        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
//        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
//    }

    override fun observeCoins(): LiveData<Result<List<Coin>>> {
        return observableCoins
    }

    override suspend fun getCoins(): Result<List<Coin>> {
        Log.v("HALOO","before")
        // Simulate network by delaying the execution.
        val tasks = coinRetreiver.getCoinsPlease()

        Log.v("HALOO",coinRetreiver.getCoinsPlease().toString())

        delay(SERVICE_LATENCY_IN_MILLIS)

        return Result.Success(tasks)
    }

    override suspend fun refreshCoins() {
        observableCoins.value = getCoins()!!
    }

    override fun observeCoin(CoinId: String): LiveData<Result<Coin>> {
        return observableCoins.map { tasks ->
            when (tasks) {
                is Result.Loading -> Result.Loading
                is Result.Error -> Result.Error(tasks.exception)
                is Result.Success -> {
                    val task = tasks.data.firstOrNull() { it.id == CoinId } ?: return@map Result.Error(
                        Exception("Not found")
                    )
                    Result.Success(task)
                }
            }
        }
    }

    override suspend fun getCoin(CoinId: String): Result<Coin> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        PRODUCTS_SERVICE_DATA[CoinId]?.let {
            return Result.Success(it)
        }
        return Result.Error(Exception("Coin not found"))
    }

    override suspend fun refreshCoin(CoinId: String) {
        refreshCoins()
    }

    override suspend fun saveCoin(Coin: Coin) {
        PRODUCTS_SERVICE_DATA[Coin.id] = Coin

    }

    override suspend fun deleteAllCoins() {
        PRODUCTS_SERVICE_DATA.clear()
    }

    override suspend fun deleteCoin(CoinId: String) {
        PRODUCTS_SERVICE_DATA.remove(CoinId)
    }

}