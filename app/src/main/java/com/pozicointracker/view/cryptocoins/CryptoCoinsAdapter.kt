package com.pozicointracker.view.cryptocoins

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pozicointracker.data.coin.model.Coin
import com.pozicointracker.data.product.model.Product
import com.pozicointracker.databinding.ItemCoinHorizontalBinding
import com.pozicointracker.databinding.ItemProductsHorizontalBinding
import com.pozicointracker.view.assets.ProductsViewModel


class CryptoCoinsAdapter(private val viewModel: CryptoCoinViewModel) : ListAdapter<Coin, CryptoCoinsAdapter.ViewHolder>(
    TaskDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }


    class ViewHolder private constructor(val binding: ItemCoinHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: CryptoCoinViewModel, item: Coin) {

            binding.viewmodel = viewModel
            binding.cryptoCoin = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCoinHorizontalBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }


}

class TaskDiffCallback : DiffUtil.ItemCallback<Coin>() {
    override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        return oldItem == newItem
    }
}
