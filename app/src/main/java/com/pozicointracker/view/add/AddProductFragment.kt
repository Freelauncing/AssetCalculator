package com.pozicointracker.view.add

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pozicointracker.AssetCalculationApplication
import com.pozicointracker.R
import com.pozicointracker.databinding.FragmentAddProductBinding
import com.pozicointracker.utility.EventObserver

class AddProductFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var viewDataBinding: FragmentAddProductBinding

    private val viewModel by viewModels<AddProductViewModel>{
        AddProductViewModelFactory((requireContext().applicationContext as AssetCalculationApplication).productRepository
            ,(requireContext().applicationContext as AssetCalculationApplication).coinRepository)
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

        checkAndStore()

        viewModel.addProduct.observe(viewLifecycleOwner, EventObserver{
            buzz(longArrayOf(100, 100, 100, 100, 100, 100))
        })

        viewModel.snackbarText.observe(viewLifecycleOwner,EventObserver{
            Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
        })

        viewModel.coinslist.observe(viewLifecycleOwner,EventObserver{
            val searchmethod = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, it)
            searchmethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            viewDataBinding.spinnerView.adapter = searchmethod
            viewDataBinding.spinnerView.onItemSelectedListener = this
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.v("Check",viewDataBinding.spinnerView.getItemAtPosition(position).toString())
        viewModel.getIndex(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    fun checkAndStore(){
        try {
            if (!check()) {
                store()
                viewModel.initialiseDropDown(true)
            }else{
                viewModel.initialiseDropDown(false)
            }
        }catch (e:Exception){

        }
    }

    fun store(){
        val sharedPreference =  requireActivity().getSharedPreferences("CHECK", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putBoolean("FIRSTTIM",true)
        editor.commit()
    }

    fun check(): Boolean{
        val sharedPreference =  requireActivity().getSharedPreferences("CHECK", Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("FIRSTTIM",false)
    }

}