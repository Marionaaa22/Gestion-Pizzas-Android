package com.mariona.gestio_pizzas_room.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Pizzes")
data class Pizzas(
    @PrimaryKey var referencia: String,
    @ColumnInfo(name = "Descripcio") var despcripcion: String,
    @ColumnInfo (name = "Tipo") var tipos: String,
    @ColumnInfo (name = "Preu") var preuSenseIVA: Float,
    @ColumnInfo (name = "IVA") var iva: Float,
)