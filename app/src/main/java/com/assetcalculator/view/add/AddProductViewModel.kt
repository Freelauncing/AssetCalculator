package com.assetcalculator.view.add

import android.net.Uri
import android.view.WindowManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assetcalculator.data.Result
import com.assetcalculator.data.product.model.Product
import com.assetcalculator.data.product.ProductRepository
import com.assetcalculator.utility.Event
import kotlinx.coroutines.launch


class AddProductViewModel(private val productRepository: ProductRepository): ViewModel() {

    val LOG_TAG:String = "AddProductViewModel"

    // Two-way databinding, exposing MutableLiveData
    val productName = MutableLiveData<String>()
    val productPurchasePrice = MutableLiveData<String>()
    val productStock = MutableLiveData<String>()

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText
    private val _addProduct = MutableLiveData<Event<Unit>>()
    val addProduct: LiveData<Event<Unit>> = _addProduct


    // Text Labels
    var productNameText: String? = null
    var purchasePriceText: String? = null
    var addProductBtnText: String? = null
    var stockText: String? = null

    init {
        initializeLabels()
    }

    private fun initializeLabels() {
        productNameText = "Asset Name"
        addProductBtnText = "Add Asset"
        purchasePriceText = "Price"
        stockText = "Total Qty"

    }

    private fun showSnackbarMessage(message: String) { //
        _snackbarText.value = Event(message)
    }


    fun onclickAddProduct(){
        val productName = productName.value
        val productPurchasePrice = productPurchasePrice.value
        val productStock = productStock.value

        if(productName == null || productName.toString() == ""){
           showSnackbarMessage("Asset Name is Missing")
       }else if(productPurchasePrice==null || productPurchasePrice.toString() == ""){
           showSnackbarMessage("Asset Price is Missing")
       } else if(productStock==null || productStock.toString() == ""){
            showSnackbarMessage("Asset Quantity is Missing")
        }else{

           viewModelScope.launch {
              val result = productRepository.getLastProduct(forceUpdate = false)
               if (result is Result.Success) {
                   productRepository.saveProduct(
                       Product(
                           (result.data as Product).product_id + 1,
                           productName,
                           String.format("%.5f",productPurchasePrice.toDouble()).toDouble(),
                           String.format("%.5f",productStock.toDouble()).toDouble()
                       ))
                   showSnackbarMessage("saved!")
                   clearInputs()
                   _addProduct.value =  Event(Unit)
               } else {
                   productRepository.saveProduct(
                       Product(
                            1,
                           productName,
                           String.format("%.5f",productPurchasePrice.toDouble()).toDouble(),
                           String.format("%.5f",productStock.toDouble()).toDouble()
                       ))
                   showSnackbarMessage("saved!")
                   clearInputs()
                   _addProduct.value =  Event(Unit)
               }


           }


       }
    }

    private fun createProduct(newTask: Product) = viewModelScope.launch {

            //_taskUpdatedEvent.value = Event(Unit)
    }

    fun clearInputs(){
        productName.value = ""
        productPurchasePrice.value = ""
        productStock.value = ""
    }

}