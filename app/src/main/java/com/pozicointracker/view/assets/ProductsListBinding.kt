package com.pozicointracker.view.assets

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pozicointracker.data.coin.model.Coin
import com.pozicointracker.data.product.model.Product
import com.pozicointracker.view.cryptocoins.CryptoCoinsAdapter

/**
 * [BindingAdapter]s for the [Product]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Product>?) {
    items?.let {
        (listView.adapter as ProductsAdapter).submitList(items)
    }
}

@BindingAdapter("app:items")
fun setItemsCoin(listView: RecyclerView, items: List<Coin>?) {
    items?.let {
        (listView.adapter as CryptoCoinsAdapter).submitList(items)
    }
}

@BindingAdapter("app:convertToString")
fun convertLongToString(textView: TextView,value:Long) {
    textView.text ="Total Items: "+ value.toString()
}

@BindingAdapter("app:totalQuantity")
fun totalQuantity(textView: TextView,value:Long) {
    textView.text ="Total Qty: "+ value.toString()
}

@BindingAdapter("app:convertToStringQuantity")
fun convertToStringQuantity(textView: TextView,value:Double) {
    textView.text = String.format("%.5f", value) + " items"
}

@BindingAdapter("app:convertToStringCoin")
fun convertToStringCoin(textView: TextView,value:Double) {
    textView.text = String.format("%.5f", value) + ""
}


@BindingAdapter("app:getmeValue")
fun convertLongToString(textView: TextView,value:Double) {
    textView.text = value.toString()+" $"
}

@BindingAdapter("app:getTotalAsset")
fun getTotalAsset(textView: TextView,list:List<Product>) {
    var sum:Double = 0.0
    list.forEach {
        sum = sum + it.product_purchase_price
    }
    textView.text = String.format("%.5f", sum) + " $"
}

@BindingAdapter("itemImage")
fun ImageView.setImage(item: String) {
    val imgUri = item.toUri()
    item?.let {
        Glide.with(context)
            .load(imgUri)
            .into(this)
    }
}