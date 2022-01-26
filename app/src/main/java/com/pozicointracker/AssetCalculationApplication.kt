package com.pozicointracker

import android.app.Application
import androidx.databinding.ktx.BuildConfig
import com.pozicointracker.data.product.ProductRepository
import com.pozicointracker.utility.ServiceLocator
import timber.log.Timber

class AssetCalculationApplication : Application() {

    val productRepository: ProductRepository
        get() = ServiceLocator.provideProductsRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

}