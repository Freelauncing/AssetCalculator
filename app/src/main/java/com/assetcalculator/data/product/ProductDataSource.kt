package com.assetcalculator.data.product

import androidx.lifecycle.LiveData
import com.assetcalculator.data.Result
import com.assetcalculator.data.product.model.Product

interface ProductDataSource {

    fun observeProducts(): LiveData<Result<List<Product>>>

    suspend fun getProducts():Result<List<Product>>

    suspend fun refreshProducts()

    fun observeProduct(ProductId: Long): LiveData<Result<Product>>

    suspend fun getProduct(ProductId: Long): Result<Product>

    suspend fun refreshProduct(ProductId: Long)

    suspend fun saveProduct(Product: Product)

    suspend fun deleteAllProducts()

    suspend fun deleteProduct(ProductId: Long)

    suspend fun getLastProduct() : Result<Product>
}