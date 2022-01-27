package com.pozicointracker.view.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pozicointracker.data.coin.CoinRepository
import com.pozicointracker.data.product.ProductRepository

class ProductsViewModelFactory(private val tasksRepository: ProductRepository,private val coinRepository: CoinRepository)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (ProductsViewModel(tasksRepository,coinRepository) as T)
}