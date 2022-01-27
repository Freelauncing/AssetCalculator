package com.pozicointracker.utility

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.pozicointracker.data.coin.CoinDataSource
import com.pozicointracker.data.coin.CoinRepository
import com.pozicointracker.data.coin.local.CoinLocalDataSource
import com.pozicointracker.data.coin.remote.CoinRemoteDataSource
import com.pozicointracker.data.coin.repository.DefaultCoinRepository
import com.pozicointracker.data.product.repository.DefaultProductRepository
import com.pozicointracker.data.product.ProductDataSource
import com.pozicointracker.data.product.ProductRepository
import com.pozicointracker.data.database.AssetsDatabase
import com.pozicointracker.data.product.local.ProductLocalDataSource
import com.pozicointracker.data.product.remote.ProductRemoteDataSource
import kotlinx.coroutines.runBlocking


object ServiceLocator {

    private val lock = Any()

    private var database: AssetsDatabase? = null

    @Volatile
    var  productsRepository: ProductRepository? = null
        @VisibleForTesting set

    @Volatile
    var  coinRepository: CoinRepository? = null
        @VisibleForTesting set

    fun provideProductsRepository(context: Context): ProductRepository {
        synchronized(this) {
            return  productsRepository ?: createProductsRepository(context)
        }
    }

    fun provideCoinRepository(context: Context): CoinRepository {
        synchronized(this) {
            return  coinRepository ?: createCoinsRepository(context)
        }
    }

    private fun createProductsRepository(context: Context): ProductRepository {
        val newRepo = DefaultProductRepository(ProductRemoteDataSource, createProductLocalDataSource(context))
        productsRepository = newRepo
        return newRepo
    }

    private fun createCoinsRepository(context: Context): CoinRepository {
        val newRepo = DefaultCoinRepository(CoinRemoteDataSource, createCoinLocalDataSource(context))
        coinRepository = newRepo
        return newRepo
    }

    private fun createProductLocalDataSource(context: Context): ProductDataSource {
        val database = database ?: createDataBase(context)
        return ProductLocalDataSource(database.productDao())
    }

    private fun createCoinLocalDataSource(context: Context): CoinDataSource {
        val database = database ?: createDataBase(context)
        return CoinLocalDataSource(database.coinDao())
    }

    private fun createDataBase(context: Context): AssetsDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            AssetsDatabase::class.java, "assets_pozi_coins.db"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                ProductRemoteDataSource.deleteAllProducts()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            productsRepository = null
            coinRepository = null
        }
    }
}