package com.mariona.gestio_pizzas_room.room

import java.io.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Pizza")
data class Pizzas(
    @PrimaryKey val reference: String,
    val description: String,
    val type: String,
    val priceWithoutTax: Double,
    val priceWithTax: Double
) : Serializable