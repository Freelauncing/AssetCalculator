package com.assetcalculator.view.edit

import android.net.Uri
import androidx.lifecycle.*
import com.assetcalculator.data.product.ProductRepository
import com.assetcalculator.data.product.model.Product
import com.assetcalculator.utility.Event
import kotlinx.coroutines.launch
import com.assetcalculator.data.Result.Success
import com.assetcalculator.data.Result
import com.assetcalculator.data.Result.Error

class EditViewModel (private val productRepository: ProductRepository): ViewModel() {

    val LOG_TAG:String = "EditProductViewModel"

    private val _productId = MutableLiveData<Long>()

    private val _product = _productId.switchMap { taskId ->
        productRepository.observeProduct(taskId).map { computeResult(it) }
    }
    val product: LiveData<Product?> = _product

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    // Two-way databinding, exposing MutableLiveData
    val productName = MutableLiveData<String>()
    val productPurchasePrice = MutableLiveData<String>()
    val productStock = MutableLiveData<String>()

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText
    private val _openChoiceDialogue = MutableLiveData<Event<Unit>>()
    val openChoiceDialogue: LiveData<Event<Unit>> = _openChoiceDialogue
    private val _updateProduct = MutableLiveData<Event<Unit>>()
    val updateProduct: LiveData<Event<Unit>> = _updateProduct

    // Text Labels
    var productNameText: String? = null
    var purchasePriceText: String? = null
    var stockText: String? = null
    var addProductBtnText: String? = null

    private var productId: Long? = null

    init {
        initializeLabels()
    }

    fun start(productId: Long?) {

        this.productId = productId

        // Trigger the load
        _productId.value = productId!!
    }

    private fun initializeLabels() {
        productNameText = "Asset Name"
        purchasePriceText = "Price"
        stockText = "Total Qty"
        addProductBtnText= "Update"
    }

    private fun showSnackbarMessage(message: String) { //
        _snackbarText.value = Event(message)
    }

    fun onclickAddImage(){
        _openChoiceDialogue.value = Event(Unit)
    }


    fun clearInputs(){
        productName.value = ""
        productPurchasePrice.value = ""
        productStock.value =""
    }

    fun setProduct(){
        val product = _product.value!!
        productName.value = product.product_name
        productPurchasePrice.value = product.product_purchase_price.toString()
        productStock.value = product.product_total_stock.toString()
    }

    private fun computeResult(taskResult: Result<Product>): Product? {
        _dataLoading.value = false
        return if (taskResult is Success) {
            taskResult.data
        } else {
            showSnackbarMessage("Error")
            null
        }
    }

    fun onclickUpdateProduct(){
        val productName = productName.value
        val productPurchasePrice = productPurchasePrice.value
        val productStock = productStock.value

         if(productName == null || productName.toString() == ""){
            showSnackbarMessage("Asset Name is Missing")
        }else if(productPurchasePrice==null || productPurchasePrice.toString() == ""){
            showSnackbarMessage("Price is Missing")
        }else if(productStock==null || productStock.toString() == ""){
            showSnackbarMessage("Asset Quantity is Missing")
        }else{
            updateProduct(
                Product(
                    productId!!,
                    productName,
                    String.format("%.5f",productPurchasePrice.toDouble()).toDouble(),
                    String.format("%.5f",productStock.toDouble()).toDouble(),
                    )
            )

        }

    }

    private fun updateProduct(product: Product) {
        viewModelScope.launch {
            productRepository.saveProduct(product)
            showSnackbarMessage("Updated")
        }
    }

}