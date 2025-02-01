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

    @Query("SELECT * FROM Pizza ORDER BY reference ASC")
    fun getAllPizzas(): MutableList<Pizzas>

    @Query("SELECT reference FROM Pizza WHERE type = :type ORDER BY reference DESC LIMIT 1")
    fun getLastReferenceByType(type: String): String?

    @Query("SELECT * FROM Pizza WHERE reference = :reference LIMIT 1")
    fun getPizzaByReference(reference: String): Pizzas?
    @Delete
    fun deletePizza(pizza: Pizzas)

    @Update
    fun updatePizza(pizza: Pizzas)
}
