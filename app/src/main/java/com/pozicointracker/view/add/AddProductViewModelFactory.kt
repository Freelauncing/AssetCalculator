package com.pozicointracker.view.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pozicointracker.data.coin.CoinRepository
import com.pozicointracker.data.product.ProductRepository

class AddProductViewModelFactory (private val productRepository: ProductRepository, private val coinRepository: CoinRepository)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (AddProductViewModel(productRepository,coinRepository) as T)
}