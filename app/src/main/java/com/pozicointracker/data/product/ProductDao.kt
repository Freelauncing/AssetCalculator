package com.pozicointracker.data.product

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pozicointracker.data.product.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun observeProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM product")
    suspend fun getProducts(): List<Product>

    @Query("SELECT * FROM product WHERE product_id = :productId")
    fun observeProductById(productId: Long): LiveData<Product>

    @Query("SELECT * FROM product WHERE product_id = :productId")
    suspend fun getProductById(productId: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product): Int

    @Query("DELETE FROM product WHERE product_id = :productId")
    suspend fun deleteProductById(productId: Long): Int

    @Query("DELETE FROM product")
    suspend fun deleteProducts()

    @Query("SELECT * FROM product WHERE product_id=(SELECT max(product_id) FROM product)")
    suspend fun getLastProduct(): Product?
}