package com.pozicointracker.view.add

import android.util.Log
import android.view.animation.Transformation
import androidx.lifecycle.*
import androidx.work.ListenableWorker
import com.pozicointracker.data.Result
import com.pozicointracker.data.coin.CoinRepository
import com.pozicointracker.data.coin.model.Coin
import com.pozicointracker.data.product.model.Product
import com.pozicointracker.data.product.ProductRepository
import com.pozicointracker.utility.Event
import kotlinx.coroutines.launch


class AddProductViewModel(private val productRepository: ProductRepository,private val coinRepository: CoinRepository): ViewModel() {

    val LOG_TAG:String = "AddProductViewModel"

    // Two-way databinding, exposing MutableLiveData
    val productName = MutableLiveData<String>()
    val productPurchasePrice = MutableLiveData<String>()
    val productStock = MutableLiveData<String>()

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText
    private val _addProduct = MutableLiveData<Event<Unit>>()
    val addProduct: LiveData<Event<Unit>> = _addProduct

    private val _coinslist = MutableLiveData<Event<List<String>>>()
    val coinslist: LiveData<Event<List<String>>> = _coinslist


    // Text Labels
    var productNameText: String? = null
    var purchasePriceText: String? = null
    var addProductBtnText: String? = null
    var stockText: String? = null

    var result = mutableListOf<String>()
    var copy:List<Coin>? = null
    var index:Int = 0

    init {
        initializeLabels()
    }

    fun initialiseDropDown(boolean: Boolean){
        viewModelScope.launch{
            var totalList:Result<List<Coin>> = coinRepository.getCoins(boolean);
            if (totalList is Result.Success) {
                copy = totalList.data
                totalList.data.map {
                    result.add(it.name)
                }
                _coinslist.value = Event(result.toList())
            }
        }

    }

    fun getIndex(int: Int){
        index = int
        productName.value = copy!!.get(int).name
        productPurchasePrice.value = copy!!.get(int).current_price.toString()
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
        try {
            val productName = productName.value
            val myproductPurchasePrice = productPurchasePrice.value
            val productStock = productStock.value
            val coinId = copy!!.get(index).id

            Log.v("TESTING",myproductPurchasePrice.toString())

            if (productName == null || productName.toString() == "" || productName == "Search Coin") {
                showSnackbarMessage("Asset Name is Missing")
            } else if (myproductPurchasePrice == null || myproductPurchasePrice.toString() == "") {
                showSnackbarMessage("Asset Price is Missing")
            } else if (productStock == null || productStock.toString() == "") {
                showSnackbarMessage("Asset Quantity is Missing")
            } else {

                viewModelScope.launch {
                    var check = false
                    var allProducts = productRepository.getProducts(false)
                    val myAllProducts = MutableLiveData<List<Product>>()
                    if (allProducts is Result.Success) {
                        myAllProducts.value = allProducts.data!!
                        myAllProducts.value!!.map {
                            if (it.product_name == productName && it.coin_id == coinId) {
                                check = true
                                updateProduct(
                                    Product(
                                        it.product_id,
                                        productName,
                                        myproductPurchasePrice.toDouble()
                                        ,

                                            it.product_total_stock + productStock.toDouble()
                                        ,
                                        coinId
                                    )
                                )
                            }

                        }
                    }

                    if (!check) {
                        val result = productRepository.getLastProduct(forceUpdate = false)
                        if (result is Result.Success) {
                            productRepository.saveProduct(
                                Product(
                                    (result.data as Product).product_id + 1,
                                    productName,
                                    myproductPurchasePrice.toDouble(),
                                    productStock.toDouble(),
                                    coinId
                                )
                            )
                            showSnackbarMessage("saved!")
                            clearInputs()
                            _addProduct.value = Event(Unit)
                            initialiseDropDown(false)
                        } else {
                            productRepository.saveProduct(
                                Product(
                                    1,
                                    productName,
                                    myproductPurchasePrice.toDouble(),
                                    productStock.toDouble(),
                                    coinId
                                )
                            )
                            showSnackbarMessage("saved!")
                            clearInputs()
                            _addProduct.value = Event(Unit)
                            initialiseDropDown(false)
                        }


                    }
                }

            }
        }catch (e:Exception){
            showSnackbarMessage("Something went wrong!")
        }
    }

    private fun updateProduct(product: Product) {
        viewModelScope.launch {
            productRepository.saveProduct(product)
            showSnackbarMessage("Saved!")
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

//for (i in it) {
//    if (i.product_name == productName && i.coin_id == coinId) {
//        check = true
//        updateProduct(
//            Product(
//                i.product_id,
//                productName,
//                String.format(
//                    "%.5f",
//                    productPurchasePrice.toDouble()
//                ).toDouble(),
//                String.format(
//                    "%.5f",
//                    i.product_total_stock + productStock.toDouble()
//                ).toDouble(),
//                coinId
//            )
//        )
//    }
//}