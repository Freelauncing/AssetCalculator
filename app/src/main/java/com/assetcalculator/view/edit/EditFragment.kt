package com.assetcalculator.view.edit

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.assetcalculator.AssetCalculationApplication
import com.assetcalculator.R
import com.assetcalculator.databinding.FragmentEditBinding
import com.assetcalculator.utility.EventObserver
import com.assetcalculator.utility.setupSnackbar
import com.google.android.material.snackbar.Snackbar


class EditFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentEditBinding

    private val args: EditFragmentArgs by navArgs()

    private val viewModel by viewModels<EditViewModel>{
        EditViewModelFactory((requireContext().applicationContext as AssetCalculationApplication).productRepository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        viewDataBinding = FragmentEditBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.start(args.productId)

        return viewDataBinding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSnackbar()

        setUpListeners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpListeners() {
        viewModel.snackbarText.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(context,"Asset Updated", Toast.LENGTH_SHORT).show()
        })
        viewModel.product.observe(viewLifecycleOwner, Observer {
            viewModel.setProduct()
        })
    }

    private fun setupSnackbar() {
       // view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
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


}