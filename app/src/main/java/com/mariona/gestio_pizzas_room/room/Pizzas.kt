package com.mariona.gestio_pizzas_room.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Pizzes")
data class Pizzas(
    @PrimaryKey val referencia: String,
    val despcripcion: String,
    val tipos: String,
    val preuSenseIVA: Float,
    val preuAmbIVA: Float,
    val iva: Float,
)