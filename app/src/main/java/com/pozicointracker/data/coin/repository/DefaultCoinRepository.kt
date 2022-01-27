package com.pozicointracker.data.coin.repository

import androidx.lifecycle.LiveData
import com.pozicointracker.data.Result
import com.pozicointracker.data.coin.CoinDataSource
import com.pozicointracker.data.coin.CoinRepository
import com.pozicointracker.data.coin.model.Coin

import com.pozicointracker.utility.wrapEspressoIdlingResource
import kotlinx.coroutines.*

class DefaultCoinRepository constructor(
    private val CoinRemoteDataSource: CoinDataSource,
    private val CoinLocalDataSource: CoinDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoinRepository {

    override suspend fun getCoins(forceUpdate: Boolean): Result<List<Coin>> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                try {
                    updateCoinsFromRemoteDataSource()
                } catch (ex: Exception) {
                    return Result.Error(ex)
                }
            }
            return CoinLocalDataSource.getCoins()
        }
    }

    override suspend fun refreshCoins() {
        wrapEspressoIdlingResource {
            updateCoinsFromRemoteDataSource()
        }
    }

    private suspend fun updateCoinsFromRemoteDataSource() {
        wrapEspressoIdlingResource {
            val remoteCoins = CoinRemoteDataSource.getCoins()

            if (remoteCoins is Result.Success) {
                // Real apps might want to do a proper sync.
                CoinLocalDataSource.deleteAllCoins()
                remoteCoins.data.forEach { task ->
                    CoinLocalDataSource.saveCoin(task)
                }
            } else if (remoteCoins is Result.Error) {
                throw remoteCoins.exception
            }
        }
    }

    override fun observeCoins(): LiveData<Result<List<Coin>>> {
        wrapEspressoIdlingResource {
            return CoinLocalDataSource.observeCoins()
        }
    }

    override suspend fun refreshCoin(CoinId: String) {
        wrapEspressoIdlingResource {
            updateTaskFromRemoteDataSource(CoinId)
        }
    }

    override fun observeCoin(CoinId: String): LiveData<Result<Coin>> {
        wrapEspressoIdlingResource {
            return CoinLocalDataSource.observeCoin(CoinId)
        }
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        wrapEspressoIdlingResource {
//            val remoteTask = CoinRemoteDataSource.getCoin(taskId)
//
//            if (remoteTask is Result.Success) {
//                CoinLocalDataSource.saveCoin(remoteTask.data)
//            }
            updateCoinsFromRemoteDataSource()
        }
    }

    override suspend fun getCoin(CoinId: String, forceUpdate: Boolean): Result<Coin> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                updateTaskFromRemoteDataSource(CoinId)
            }
            return CoinLocalDataSource.getCoin(CoinId)
        }
    }

    override suspend fun saveCoin(Coin: Coin) {
        wrapEspressoIdlingResource {
            coroutineScope() {
                launch { CoinRemoteDataSource.saveCoin(Coin) }
                launch { CoinLocalDataSource.saveCoin(Coin) }
            }
        }
    }

    override suspend fun deleteAllCoins() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                coroutineScope {
                    launch { CoinRemoteDataSource.deleteAllCoins() }
                    launch { CoinLocalDataSource.deleteAllCoins() }
                }
            }
        }
    }

    override suspend fun deleteCoin(CoinId: String) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { CoinRemoteDataSource.deleteCoin(CoinId) }
                launch { CoinLocalDataSource.deleteCoin(CoinId) }
            }
        }
    }
}