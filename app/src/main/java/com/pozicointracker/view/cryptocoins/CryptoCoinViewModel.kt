package com.pozicointracker.view.cryptocoins

import android.util.Log
import androidx.lifecycle.*
import com.pozicointracker.data.Result
import com.pozicointracker.data.coin.CoinRepository
import com.pozicointracker.data.coin.model.Coin
import com.pozicointracker.data.product.model.Product
import com.pozicointracker.utility.Event
import kotlinx.coroutines.launch

class CryptoCoinViewModel(private val coinRepository: CoinRepository): ViewModel(){

    val LOG_TAG:String = "ProductsViewModel"

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    val filter_String = MutableLiveData<String>()

    private val _items: LiveData<List<Coin>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                coinRepository.refreshCoins()
                _dataLoading.value = false
            }
        }
        coinRepository.observeCoins().switchMap { filterAndConvertProducts(it) }
    }

    val items: LiveData<List<Coin>> = _items

    // Not used at the moment
    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val _openTaskEvent = MutableLiveData<Event<Long>>()
    val openTaskEvent: LiveData<Event<Long>> = _openTaskEvent

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    private fun showSnackbarMessage(message: String) { //
        _snackbarText.value = Event(message)
    }

    fun openTask(taskId: Long) { //
        _openTaskEvent.value = Event(taskId)
    }


    private fun filterAndConvertProducts(productsResult: Result<List<Coin>>): LiveData<List<Coin>> { //
        // TODO: This is a good case for liveData builder. Replace when stable.
        val result = MutableLiveData<List<Coin>>()

        if (productsResult is Result.Success) {
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

    private fun filterProducts(products: List<Coin>): List<Coin> { //
        val filterStringCopy = filter_String.value

        if(filterStringCopy!=null){

            Log.v(LOG_TAG,filterStringCopy.toString())
            val productsToShow = ArrayList<Coin>()
            // We filter the products based on the requestType
            for (product in products) {
//                Log.v(LOG_TAG,product.product_name.toString())
//                Log.v(LOG_TAG,product.product_name.contains(filterStringCopy.toString()).toString())
                if(product.name.contains(filterStringCopy.toString(),ignoreCase = true)){
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

//    private fun filterAndConvertProducts(productsResult: Result<List<Coin>>): LiveData<List<Coin>> { //
//        // TODO: This is a good case for liveData builder. Replace when stable.
//        val result = MutableLiveData<List<Coin>>()
//
//        if (productsResult is Result.Success) {
//
//            viewModelScope.launch {
//                Log.v("Kaloo",productsResult.data.toString())
//            }
//        } else {
//
//        }
//        Log.v("CHECKU", result.value.toString())
//
//        return result
//    }

//    fun loadTasks(forceUpdate: Boolean) { //
//      //  _forceUpdate.value = forceUpdate
//        viewModelScope.launch {
//            coinRepository.refreshCoins()
//        }
//    }
}