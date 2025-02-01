package com.mariona.gestio_pizzas_room.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
@Dao
interface PizzasDao {

    // Función para obtener todas las pizzas
    @Query("SELECT * FROM Pizzes ORDER BY referencia ASC")
    fun getPizzes(): MutableList<Pizzas>

    // Función para obtener la última referencia
    @Query("SELECT COALESCE(MAX(referencia), 0) FROM Pizzes")
    fun getLastReferenciaByType(): Long

    // Función para eliminar una pizza
    @Delete
    fun deletePizza(pizzes: Pizzas)

    // Función para actualizar una pizza
    @Update
    fun updatePizza(pizzes: Pizzas)

    // Función para insertar una pizza
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPizza(pizzes: Pizzas): Long

}