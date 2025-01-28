package com.mariona.gestio_pizzas_room

import android.app.Application
import androidx.room.Room
import com.mariona.gestio_pizzas_room.room.PizzasDataBase

class AppDB : Application() {
    lateinit var db: PizzasDataBase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            PizzasDataBase::class.java,
            "pizzas-db"
        ).build()
    }
}