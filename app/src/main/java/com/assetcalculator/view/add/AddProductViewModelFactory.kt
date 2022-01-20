package com.assetcalculator.view.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.assetcalculator.data.product.ProductRepository
import com.assetcalculator.view.add.AddProductViewModel

class AddProductViewModelFactory (private val productRepository: ProductRepository)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (AddProductViewModel(productRepository) as T)
}