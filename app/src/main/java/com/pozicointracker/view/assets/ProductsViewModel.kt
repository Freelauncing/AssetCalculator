package com.pozicointracker.view.assets

import android.util.Log
import androidx.lifecycle.*
import com.pozicointracker.data.product.ProductRepository
import com.pozicointracker.data.product.model.Product
import com.pozicointracker.utility.Event
import kotlinx.coroutines.launch
import com.pozicointracker.data.Result
import com.pozicointracker.data.Result.Success
import com.pozicointracker.data.coin.CoinRepository
import com.pozicointracker.data.coin.model.Coin

class ProductsViewModel(private val productRepository: ProductRepository,private val coinRepository: CoinRepository):ViewModel() {

    val LOG_TAG:String = "ProductsViewModel"

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    val filter_String = MutableLiveData<String>()

    var copy:List<Coin>? = null
    var prod:List<Product>? = null
    var newProd = ArrayList<Product>()

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


    fun fecthProducts(boolean: Boolean){
        viewModelScope.launch{
            var ll:Result<List<Coin>> = coinRepository.getCoins(boolean);
            if (ll is Result.Success) {
                copy = ll.data
            }

            var totalList:Result<List<Product>> = productRepository.getProducts(boolean)
            Log.v("Thulu",totalList.toString())
            if (totalList is Result.Success) {
                prod = totalList.data
            }

            for(p in prod!!){
                for(c in copy!!){
                    if(p.coin_id == c.id){
                        newProd.add(Product(p.product_id,p.product_name,c.current_price,p.product_total_stock,p.coin_id))
                        break
                    }
                }
            }

            productRepository.deleteAllProducts()
            newProd.forEach { prod ->
                    productRepository.saveProduct(prod)
            }
        }
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
        fecthProducts(true)
    }

    fun onClickDelete(id:Long) = viewModelScope.launch {
        id.let {
            productRepository.deleteProduct(it)
            showSnackbarMessage("Asset Deleted")
        }
    }

}