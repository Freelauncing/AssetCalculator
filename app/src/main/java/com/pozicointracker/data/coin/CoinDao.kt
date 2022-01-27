package com.pozicointracker.data.coin

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pozicointracker.data.coin.model.Coin

@Dao
interface CoinDao {

    @Query("SELECT * FROM coin")
    fun observeCoins(): LiveData<List<Coin>>

    @Query("SELECT * FROM coin")
    suspend fun getCoins(): List<Coin>

    @Query("SELECT * FROM coin WHERE id = :coinId")
    fun observeCoinById(coinId: String): LiveData<Coin>

    @Query("SELECT * FROM coin WHERE id = :coinId")
    suspend fun getCoinById(coinId: String): Coin?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: Coin)

    @Update
    suspend fun updateCoin(coin: Coin): Int

    @Query("DELETE FROM coin WHERE id = :coinId")
    suspend fun deleteCoinById(coinId: String): Int

    @Query("DELETE FROM coin")
    suspend fun deleteCoins()

}