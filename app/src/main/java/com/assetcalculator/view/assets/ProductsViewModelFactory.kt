package com.assetcalculator.view.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.assetcalculator.data.product.ProductRepository

class ProductsViewModelFactory(private val tasksRepository: ProductRepository)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (ProductsViewModel(tasksRepository) as T)
}