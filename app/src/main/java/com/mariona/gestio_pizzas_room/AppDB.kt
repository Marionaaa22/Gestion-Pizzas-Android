package com.mariona.gestio_pizzas_room

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mariona.gestio_pizzas_room.room.Pizzas
import com.mariona.gestio_pizzas_room.room.PizzasDao
import com.mariona.gestio_pizzas_room.room.AppDatabase

@Database(entities = [Pizzas::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun pizzaDao(): PizzasDao
}