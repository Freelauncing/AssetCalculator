package com.assetcalculator.data.product.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.assetcalculator.data.Result
import com.assetcalculator.data.Result.Loading
import com.assetcalculator.data.Result.Success
import com.assetcalculator.data.Result.Error
import com.assetcalculator.data.product.ProductDataSource
import com.assetcalculator.data.product.model.Product
import kotlinx.coroutines.delay

object ProductRemoteDataSource : ProductDataSource{

    private val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var PRODUCTS_SERVICE_DATA = LinkedHashMap<Long, Product>(2)

    private val observableProducts = MutableLiveData<Result<List<Product>>>()

    //    init {
//        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
//        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
//    }

    override fun observeProducts(): LiveData<Result<List<Product>>> {
        return observableProducts
    }

    override suspend fun getProducts(): Result<List<Product>> {
        // Simulate network by delaying the execution.
        val tasks = PRODUCTS_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLIS)
        return Success(tasks)
    }

    override suspend fun refreshProducts() {
        observableProducts.value = getProducts()!!
    }

    override fun observeProduct(ProductId: Long): LiveData<Result<Product>> {
        return observableProducts.map { tasks ->
            when (tasks) {
                is Loading -> Loading
                is Error -> Error(tasks.exception)
                is Success -> {
                    val task = tasks.data.firstOrNull() { it.product_id == ProductId } ?: return@map Error(Exception("Not found"))
                    Success(task)
                }
            }
        }
    }

    override suspend fun getProduct(ProductId: Long): Result<Product> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        PRODUCTS_SERVICE_DATA[ProductId]?.let {
            return Success(it)
        }
        return Error(Exception("Product not found"))
    }

    override suspend fun refreshProduct(ProductId: Long) {
        refreshProducts()
    }

    override suspend fun saveProduct(product: Product) {
        PRODUCTS_SERVICE_DATA[product.product_id] = product
    }

    override suspend fun deleteAllProducts() {
        PRODUCTS_SERVICE_DATA.clear()
    }

    override suspend fun deleteProduct(ProductId: Long) {
        PRODUCTS_SERVICE_DATA.remove(ProductId)
    }

    override suspend fun getLastProduct(): Result<Product> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        //you entered Map<Long, String> entry
        var id:Long = 0
        for (key in PRODUCTS_SERVICE_DATA.keys){
            id= PRODUCTS_SERVICE_DATA[key]?.product_id ?: 0L
        }

        if(id != 0L){
            return Success(PRODUCTS_SERVICE_DATA[id]!!)
        }

        return Error(Exception("Product not found"))
    }
}