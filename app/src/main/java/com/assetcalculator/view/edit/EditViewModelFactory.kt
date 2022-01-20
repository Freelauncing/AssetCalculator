package com.assetcalculator.view.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.assetcalculator.data.product.ProductRepository

class EditViewModelFactory (private val productRepository: ProductRepository)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (EditViewModel(productRepository) as T)
}