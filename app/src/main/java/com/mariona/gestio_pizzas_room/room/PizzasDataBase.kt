package com.mariona.gestio_pizzas_room.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Pizzas::class], version = 3)
abstract class PizzasDataBase : RoomDatabase() {
    abstract fun pizzasDao(): PizzasDao

    companion object {
        @Volatile
        private var INSTANCE: PizzasDataBase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PizzasDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PizzasDataBase::class.java,
                    "pizzas-db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}