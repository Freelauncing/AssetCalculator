package com.assetcalculator.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assetcalculator.data.product.model.Product
import com.assetcalculator.data.product.ProductDao

/**
 * The Room Database that contains the Product table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [Product::class], version = 2, exportSchema = false)
abstract class AssetsDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
}
