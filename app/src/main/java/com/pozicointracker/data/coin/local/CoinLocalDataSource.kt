package com.pozicointracker.data.coin.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.pozicointracker.data.Result
import com.pozicointracker.data.coin.CoinDao
import com.pozicointracker.data.coin.CoinDataSource
import com.pozicointracker.data.coin.model.Coin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CoinLocalDataSource internal constructor(
    private val coinDao: CoinDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoinDataSource {

    override fun observeCoins(): LiveData<Result<List<Coin>>> {
        return coinDao.observeCoins().map {
            Result.Success(it)
        }
    }

    override suspend fun getCoins(): Result<List<Coin>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(coinDao.getCoins())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun refreshCoins() {

    }

    override fun observeCoin(CoinId: String): LiveData<Result<Coin>> {
        return coinDao.observeCoinById(CoinId).map {
            Result.Success(it)
        }
    }

    override suspend fun getCoin(CoinId: String): Result<Coin> = withContext(ioDispatcher) {
        try {
            val task = coinDao.getCoinById(CoinId)
            if (task != null) {
                return@withContext Result.Success(task)
            } else {
                return@withContext Result.Error(Exception("Coin not found!"))
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    override suspend fun refreshCoin(CoinId: String) {

    }

    override suspend fun saveCoin(Coin: Coin) = withContext(ioDispatcher) {
        coinDao.insertCoin(Coin)
    }

    override suspend fun deleteAllCoins() = withContext(ioDispatcher) {
        coinDao.deleteCoins()
    }

    override suspend fun deleteCoin(CoinId: String) = withContext<Unit>(ioDispatcher) {
        coinDao.deleteCoinById(CoinId)
    }

}