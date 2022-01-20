package com.assetcalculator.view.assets

import android.util.Log
import androidx.lifecycle.*
import com.assetcalculator.data.product.ProductRepository
import com.assetcalculator.data.product.model.Product
import com.assetcalculator.utility.Event
import kotlinx.coroutines.launch
import com.assetcalculator.data.Result
import com.assetcalculator.data.Result.Success

class ProductsViewModel(private val productRepository: ProductRepository):ViewModel() {

    val LOG_TAG:String = "ProductsViewModel"

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    val filter_String = MutableLiveData<String>()

    private val _items: LiveData<List<Product>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                productRepository.refreshProducts()
                _dataLoading.value = false
            }
        }
        productRepository.observeProducts().switchMap { filterAndConvertProducts(it) }
    }

    val items: LiveData<List<Product>> = _items

    val total : LiveData<String> = Transformations.map(_items){
        var sum:Double = 0.0
        it.map {
            sum = sum + (it.product_purchase_price * it.product_total_stock)
        }
        Log.v("KALOO",sum.toString())
        var res:String  = String.format("%.5f",sum)
        res = "Total Value: "+res + " $"
        res
    }

    val totalQty : LiveData<String> = Transformations.map(_items){
        var sum:Double = 0.0
        it.map {
            sum = sum + it.product_total_stock
        }
        Log.v("KALOO",sum.toString())
        var res:String  = String.format("%.5f",sum)
        res= "Total Qty: "+sum
        res
    }

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }


    // Not used at the moment
    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val _openTaskEvent = MutableLiveData<Event<Long>>()
    val openTaskEvent: LiveData<Event<Long>> = _openTaskEvent

    // Text Labels
    var allProductText: String? = null

    init {
        initializeLabels()
    }

    private fun initializeLabels() {
        allProductText="All Products"
    }

    private fun showSnackbarMessage(message: String) { //
        _snackbarText.value = Event(message)
    }

    fun openTask(taskId: Long) { //
        _openTaskEvent.value = Event(taskId)
    }


    private fun filterAndConvertProducts(productsResult: Result<List<Product>>): LiveData<List<Product>> { //
        // TODO: This is a good case for liveData builder. Replace when stable.
        val result = MutableLiveData<List<Product>>()

        if (productsResult is Success) {
            isDataLoadingError.value = false
            viewModelScope.launch {
                result.value = filterProducts(productsResult.data)
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage("R.string.loading_products_error")
            isDataLoadingError.value = true
        }

        return result
    }

    private fun filterProducts(products: List<Product>): List<Product> { //
        val filterStringCopy = filter_String.value
        if(filterStringCopy!=null){
            Log.v(LOG_TAG,filterStringCopy.toString())
            val productsToShow = ArrayList<Product>()
            // We filter the products based on the requestType
            for (product in products) {
                Log.v(LOG_TAG,product.product_name.toString())
                Log.v(LOG_TAG,product.product_name.contains(filterStringCopy.toString()).toString())
                if(product.product_name.contains(filterStringCopy.toString())){
                    productsToShow.add(product)
                }
            }
            return productsToShow
        }

        return products
    }

    fun loadTasks(forceUpdate: Boolean) { //
        _forceUpdate.value = forceUpdate
    }

    fun refresh() { //
        _forceUpdate.value = true
    }

    fun onClickDelete(id:Long) = viewModelScope.launch {
        id.let {
            productRepository.deleteProduct(it)
            showSnackbarMessage("Asset Deleted")
        }
    }

}