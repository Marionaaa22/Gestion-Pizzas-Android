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

    // Función para insertar una pizza
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPizza(pizzes: Pizzas): Long

    // Función para eliminar una pizza
    @Delete
    fun delete(pizzes: Pizzas)

    // Función para actualizar una pizza
    @Update
    fun update(pizzes: Pizzas)

    // Función para eliminar todas las pizzas
    @Query("DELETE FROM Pizzes")
    fun deleteAll()

    // Función para obtener la última referencia
    @Query("SELECT COALESCE(MAX(referencia), 0) FROM Pizzes")
    fun getLastReferencia(): Long

    //Funcion para que actualizar el IVA
    @Query("UPDATE Pizzes SET IVA = :iva")
    fun updateIva(iva: Float)

    //Funcion para obtener el IVA
    @Query("SELECT IVA FROM Pizzes LIMIT 1")
    fun getIva(): Float
}