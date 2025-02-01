package com.mariona.gestio_pizzas_room.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Pizza") // Nombre de la tabla
data class Pizzas(
    @PrimaryKey val reference: String, // La clave primaria
    val description: String,
    val type: String,
    val priceWithoutTax: Double,
    val priceWithTax: Double
)