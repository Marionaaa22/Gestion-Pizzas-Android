package com.mariona.gestio_pizzas_room.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PizzasDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPizza(pizza: Pizzas)

    @Query("SELECT * FROM Pizza ORDER BY referencia ASC")
    fun getAllPizzas(): MutableList<Pizzas>

    @Query("SELECT referencia FROM Pizza WHERE tipo = :type ORDER BY referencia DESC LIMIT 1")
    fun getLastReferenceByType(type: String): String?

    @Query("SELECT * FROM Pizza WHERE referencia = :reference LIMIT 1")
    fun getPizzaByReference(reference: String): Pizzas?

    @Query("SELECT * FROM Pizza WHERE tipo = :type")
    fun getPizzasByType(type: String): List<Pizzas>

    @Query("SELECT * FROM Pizza WHERE descripcion IS NULL OR descripcion = :descripcion")
    fun getPizzasByDescripcion(descripcion: String): List<Pizzas>

    @Delete
    fun deletePizza(pizza: Pizzas)

    @Update
    fun updatePizza(pizza: Pizzas)
}
