package com.pozicointracker.utility

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
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

    fun provideProductsRepository(context: Context): ProductRepository {
        synchronized(this) {
            return  productsRepository ?: createProductsRepository(context)
        }
    }

    private fun createProductsRepository(context: Context): ProductRepository {
        val newRepo = DefaultProductRepository(ProductRemoteDataSource, createProductLocalDataSource(context))
        productsRepository = newRepo
        return newRepo
    }

    private fun createProductLocalDataSource(context: Context): ProductDataSource {
        val database = database ?: createDataBase(context)
        return ProductLocalDataSource(database.productDao())
    }

    private fun createDataBase(context: Context): AssetsDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            AssetsDatabase::class.java, "assets_.db"
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
        }
    }
}