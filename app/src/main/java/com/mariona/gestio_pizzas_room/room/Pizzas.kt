package com.mariona.gestio_pizzas_room.room

import java.io.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Pizza")
data class Pizzas(
    @PrimaryKey val referencia: String,
    val descripcion: String,
    val tipo: String,
    val precio: Double,
    val precioIVA: Double
) : Serializable