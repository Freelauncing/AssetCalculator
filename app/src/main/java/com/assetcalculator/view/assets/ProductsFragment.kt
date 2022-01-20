package com.assetcalculator.view.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.assetcalculator.AssetCalculationApplication
import com.assetcalculator.R
import com.assetcalculator.databinding.FragmentProductsBinding
import com.assetcalculator.utility.EventObserver
import com.assetcalculator.utility.setupSnackbar
import com.google.android.material.snackbar.Snackbar
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