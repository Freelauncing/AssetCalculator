package com.pozicointracker.data.product.repository

import androidx.lifecycle.LiveData
import com.pozicointracker.data.product.model.Product
import com.pozicointracker.utility.wrapEspressoIdlingResource
import com.pozicointracker.data.Result
import com.pozicointracker.data.Result.Success
import com.pozicointracker.data.Result.Error
import com.pozicointracker.data.product.ProductDataSource
import com.pozicointracker.data.product.ProductRepository
import kotlinx.coroutines.*

class DefaultProductRepository constructor(
    private val productRemoteDataSource: ProductDataSource,
    private val productLocalDataSource: ProductDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductRepository {
    
    override suspend fun getProducts(forceUpdate: Boolean): Result<List<Product>> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                try {
                    updateProductsFromRemoteDataSource()
                } catch (ex: Exception) {
                    return Error(ex)
                }
            }
            return productLocalDataSource.getProducts()
        }
    }

    override suspend fun refreshProducts() {
        wrapEspressoIdlingResource {
            updateProductsFromRemoteDataSource()
        }
    }

    private suspend fun updateProductsFromRemoteDataSource() {
        wrapEspressoIdlingResource {
            val remoteProducts = productRemoteDataSource.getProducts()

            if (remoteProducts is Success) {
                // Real apps might want to do a proper sync.
//                ProductsLocalDataSource.deleteAllProducts()
                remoteProducts.data.forEach { task ->
                    productLocalDataSource.saveProduct(task)
                }
            } else if (remoteProducts is Error) {
                throw remoteProducts.exception
            }
        }
    }

    override fun observeProducts(): LiveData<Result<List<Product>>> {
        wrapEspressoIdlingResource {
            return productLocalDataSource.observeProducts()
        }
    }

    override suspend fun refreshProduct(productId: Long) {
        wrapEspressoIdlingResource {
            updateTaskFromRemoteDataSource(productId)
        }
    }

    override fun observeProduct(productId: Long): LiveData<Result<Product>> {
        wrapEspressoIdlingResource {
            return productLocalDataSource.observeProduct(productId)
        }
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: Long) {
        wrapEspressoIdlingResource {
            val remoteTask = productRemoteDataSource.getProduct(taskId)

            if (remoteTask is Success) {
                productLocalDataSource.saveProduct(remoteTask.data)
            }
        }
    }

    override suspend fun getProduct(productId: Long, forceUpdate: Boolean): Result<Product> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                updateTaskFromRemoteDataSource(productId)
            }
            return productLocalDataSource.getProduct(productId)
        }
    }

    override suspend fun saveProduct(product: Product) {
        wrapEspressoIdlingResource {
            coroutineScope() {
                launch { productRemoteDataSource.saveProduct(product) }
                launch { productLocalDataSource.saveProduct(product) }
            }
        }
    }

    override suspend fun deleteAllProducts() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                coroutineScope {
                    launch { productRemoteDataSource.deleteAllProducts() }
                    launch { productLocalDataSource.deleteAllProducts() }
                }
            }
        }
    }

    override suspend fun deleteProduct(productId: Long) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { productRemoteDataSource.deleteProduct(productId) }
                launch { productLocalDataSource.deleteProduct(productId) }
            }
        }
    }

    override suspend fun getLastProduct(forceUpdate: Boolean): Result<Product> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                updateProductsFromRemoteDataSource()
            }
            return productLocalDataSource.getLastProduct()
        }
    }
}