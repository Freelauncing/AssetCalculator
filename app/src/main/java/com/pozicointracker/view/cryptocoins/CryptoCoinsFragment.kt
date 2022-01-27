package com.pozicointracker.view.cryptocoins

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.pozicointracker.AssetCalculationApplication
import com.pozicointracker.R
import com.pozicointracker.databinding.FragmentCryptoCoinsBinding
import com.pozicointracker.databinding.FragmentProductsBinding
import com.pozicointracker.utility.EventObserver
import com.pozicointracker.utility.setupSnackbar
import com.pozicointracker.view.assets.ProductsAdapter
import com.pozicointracker.view.assets.ProductsFragmentDirections
import timber.log.Timber
import androidx.lifecycle.Observer


class CryptoCoinsFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentCryptoCoinsBinding

    private lateinit var listAdapter: CryptoCoinsAdapter

    private val viewModel by viewModels<CryptoCoinViewModel>{
        CryptoCoinViewModelFactory((requireContext().applicationContext as AssetCalculationApplication).coinRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crypto_coins, container, false)

        viewDataBinding = FragmentCryptoCoinsBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSnackbar()

        setUpListeners()

        setupListAdapter()

    }

    private fun setUpListeners() {

        viewModel.filter_String.observe(viewLifecycleOwner, Observer {
            viewModel.loadTasks(false)
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    override fun onPause() {
        super.onPause()
        if(viewModel.snackbarText.hasActiveObservers())
            viewModel.snackbarText.removeObservers(this)
    }

    override fun onResume() {
        super.onResume()
        if(!viewModel.snackbarText.hasActiveObservers())
            setupSnackbar()
    }

    private fun setupListAdapter() {

        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = CryptoCoinsAdapter(viewModel)
            viewDataBinding.productsList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

}