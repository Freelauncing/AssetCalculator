package com.pozicointracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pozicointracker.data.coin.CoinDao
import com.pozicointracker.data.product.model.Product
import com.pozicointracker.data.product.ProductDao
import com.pozicointracker.data.coin.model.Coin

/**
 * The Room Database that contains the Product table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [Product::class, Coin::class], version = 4, exportSchema = false)
abstract class AssetsDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun coinDao(): CoinDao
}
