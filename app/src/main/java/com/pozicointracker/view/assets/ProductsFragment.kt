package com.pozicointracker.view.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.pozicointracker.AssetCalculationApplication
import com.pozicointracker.R
import com.pozicointracker.databinding.FragmentProductsBinding
import com.pozicointracker.utility.EventObserver
import com.pozicointracker.utility.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_products.*
import timber.log.Timber

class ProductsFragment : Fragment() {

    val LOG_TAG:String = "ProductsFragment"

    private lateinit var viewDataBinding: FragmentProductsBinding

    private lateinit var listAdapter: ProductsAdapter

    private val viewModel by viewModels<ProductsViewModel>{
        ProductsViewModelFactory((requireContext().applicationContext as AssetCalculationApplication).productRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        viewDataBinding = FragmentProductsBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        MobileAds.initialize(context)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSnackbar()

        setUpListeners()

        setupListAdapter()

        viewDataBinding.floatingActionButton.setOnClickListener {
            val action = ProductsFragmentDirections.actionProductsFragmentToAddProductFragment()
            findNavController().navigate(action)
        }

        setUpAd()
    }

    private fun setUpAd() {
        val adRequest = AdRequest.Builder().build()

        viewDataBinding.adView.loadAd(adRequest)

        viewDataBinding.adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }


            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }

    }

    private fun setUpListeners() {

        viewModel.filter_String.observe(viewLifecycleOwner, Observer {
            viewModel.loadTasks(false)
        })

        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
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
            listAdapter = ProductsAdapter(viewModel)
            viewDataBinding.productsList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun openTaskDetails(productId: Long) {
        val action = ProductsFragmentDirections.actionProductsFragmentToEditFragment(productId)
        findNavController().navigate(action)
    }
}