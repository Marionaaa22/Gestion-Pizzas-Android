package com.mariona.gestio_pizzas_room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mariona.gestio_pizzas_room.room.Pizzas
import com.mariona.gestio_pizzas_room.room.PizzasDao

@Database(entities = [Pizzas::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun pizzaDao(): PizzasDao
}
