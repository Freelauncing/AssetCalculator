package com.pozicointracker.data.product.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product @JvmOverloads constructor(

    @PrimaryKey
    @ColumnInfo(name = "product_id")
    var product_id : Long ,

    @ColumnInfo(name = "product_name")
    var product_name: String = "",

    @ColumnInfo(name = "product_purchase_price")
    var product_purchase_price: Double = 0.0,

    @ColumnInfo(name = "product_total_stock")
    var product_total_stock: Double = 0.0,

    @ColumnInfo(name = "coin_id")
    var coin_id: String = "",
)