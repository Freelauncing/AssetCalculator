package com.pozicointracker.view.cryptocoins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pozicointracker.data.coin.CoinRepository

class CryptoCoinViewModelFactory(private val coinRepository: CoinRepository)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (CryptoCoinViewModel(coinRepository) as T)
}