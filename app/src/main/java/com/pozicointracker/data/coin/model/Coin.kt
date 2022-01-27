package com.pozicointracker.data.coin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Coin")
data class Coin @JvmOverloads constructor(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id : String ,

    @ColumnInfo(name = "symbol")
    var symbol : String ,

    @ColumnInfo(name = "name")
    var name : String ,

    @ColumnInfo(name = "image")
    var image : String ,

    @ColumnInfo(name = "current_price")
    var current_price : Double ,

    @ColumnInfo(name = "market_cap")
    var market_cap : Double ,

    @ColumnInfo(name = "market_cap_rank")
    var market_cap_rank : Double ,

    @ColumnInfo(name = "total_volume")
    var total_volume : Double ,

    @ColumnInfo(name = "high_24h")
    var high_24h : Double ,

    @ColumnInfo(name = "low_24h")
    var low_24h : Double ,

    @ColumnInfo(name = "price_change_24h")
    var price_change_24h : Double ,

    @ColumnInfo(name = "price_change_percentage_24h")
    var price_change_percentage_24h : Double ,

    @ColumnInfo(name = "circulating_supply")
    var circulating_supply : Double ,

    @ColumnInfo(name = "total_supply")
    var total_supply : Double ,

    @ColumnInfo(name = "max_supply")
    var max_supply : Double ,

    @ColumnInfo(name = "last_updated")
    var last_updated : String ,

)
