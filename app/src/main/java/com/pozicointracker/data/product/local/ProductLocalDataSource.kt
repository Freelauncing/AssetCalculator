package com.pozicointracker.data.product.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.pozicointracker.data.product.ProductDataSource
import com.pozicointracker.data.product.model.Product
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import com.pozicointracker.data.Result
import com.pozicointracker.data.Result.Error
import com.pozicointracker.data.Result.Success
import com.pozicointracker.data.product.ProductDao
import kotlinx.coroutines.withContext

class ProductLocalDataSource internal constructor(
    private val productDao: ProductDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductDataSource {

    override fun observeProducts(): LiveData<Result<List<Product>>> {
        return productDao.observeProducts().map {
            Success(it)
        }
    }

    override suspend fun getProducts(): Result<List<Product>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(productDao.getProducts())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun refreshProducts() {

    }

    override fun observeProduct(ProductId: Long): LiveData<Result<Product>> {
        return productDao.observeProductById(ProductId).map {
            Success(it)
        }
    }

    override suspend fun getProduct(ProductId: Long): Result<Product> = withContext(ioDispatcher) {
        try {
            val task = productDao.getProductById(ProductId)
            if (task != null) {
                return@withContext Success(task)
            } else {
                return@withContext Error(Exception("Product not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun refreshProduct(ProductId: Long) {

    }

    override suspend fun saveProduct(product: Product) = withContext(ioDispatcher) {
        productDao.insertProduct(product)
    }

    override suspend fun deleteAllProducts() = withContext(ioDispatcher) {
        productDao.deleteProducts()
    }

    override suspend fun deleteProduct(ProductId: Long) = withContext<Unit>(ioDispatcher) {
        productDao.deleteProductById(ProductId)
    }

    override suspend fun getLastProduct(): Result<Product> = withContext(ioDispatcher) {
        try {
            val task = productDao.getLastProduct()
            if (task != null) {
                return@withContext Success(task)
            } else {
                return@withContext Error(Exception("Product not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

}