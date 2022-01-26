package com.pozicointracker.view.add

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pozicointracker.AssetCalculationApplication
import com.pozicointracker.R
import com.pozicointracker.databinding.FragmentAddProductBinding
import com.pozicointracker.utility.EventObserver


class AddProductFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentAddProductBinding

    private val viewModel by viewModels<AddProductViewModel>{
        AddProductViewModelFactory((requireContext().applicationContext as AssetCalculationApplication).productRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        viewDataBinding = FragmentAddProductBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSnackbar()

        viewModel.addProduct.observe(viewLifecycleOwner, EventObserver{
            buzz(longArrayOf(100, 100, 100, 100, 100, 100))
        })

        viewModel.snackbarText.observe(viewLifecycleOwner,EventObserver{
            Toast.makeText(context,"Asset Saved",Toast.LENGTH_SHORT).show()
        })
    }


    private fun setupSnackbar() {
     //   view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
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


    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()
        buzzer?.let {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }

}