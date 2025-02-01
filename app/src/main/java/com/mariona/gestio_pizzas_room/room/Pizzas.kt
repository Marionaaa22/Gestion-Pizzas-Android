package com.mariona.gestio_pizzas_room.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Pizza") // Nombre de la tabla
data class Pizzas(
    @PrimaryKey val referencia: String, // La clave primaria
    val descripcio: String,
    val tipo: String,
    val preu: Double,
    val preuIVA: Double
)