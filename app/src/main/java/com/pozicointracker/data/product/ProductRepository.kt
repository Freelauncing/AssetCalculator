package com.pozicointracker.data.product

import androidx.lifecycle.LiveData
import com.pozicointracker.data.product.model.Product
import com.pozicointracker.data.Result

interface ProductRepository {

    fun observeProducts(): LiveData<Result<List<Product>>>

    suspend fun getProducts(forceUpdate: Boolean = false): Result<List<Product>>

    suspend fun refreshProducts()

    suspend fun refreshProduct(productId: Long)

    fun observeProduct(productId: Long): LiveData<Result<Product>>

    suspend fun getProduct(productId: Long, forceUpdate: Boolean = false): Result<Product>

    suspend fun saveProduct(product: Product)

    suspend fun deleteAllProducts()

    suspend fun deleteProduct(productId: Long)

    suspend fun getLastProduct(forceUpdate: Boolean = false): Result<Product>

}