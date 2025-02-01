package com.mariona.gestio_pizzas_room

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mariona.gestio_pizzas_room.adapter.pizzaAdapter
import com.mariona.gestio_pizzas_room.room.AppDatabase
import com.mariona.gestio_pizzas_room.room.Pizzas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: pizzaAdapter
    private val pizzaList = mutableListOf<Pizzas>()
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "pizza-database"
        ).build()

        // Initialize the adapter
        adapter = pizzaAdapter(pizzaList, onDelete = { pizza ->
            // Handle delete action
        }, onEdit = { pizza ->
            // Handle edit action
        })

        // Set up the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load pizzas from the database
        lifecycleScope.launch {
            loadPizzasFromDatabase()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mAfegirPizza -> {
                val intent = Intent(this, addPizza::class.java)
                startActivity(intent)
                return true
            }
            R.id.mFiltrar -> return true
            R.id.mPizzes -> {
                filterPizzas("PIZZA")
                return true
            }
            R.id.mPizzesVeganes -> {
                filterPizzas("PIZZA VEGANA")
                return true
            }
            R.id.mPizzesCeliacas -> {
                filterPizzas("PIZZA CELIACA")
                return true
            }
            R.id.mToppings -> {
                filterPizzas("TOPPING")
                return true
            }
            R.id.mDescripcio -> {
                sortPizzasByDescription()
                return true
            }
            R.id.mOrdenar -> return true
            R.id.mOrdenarAZ -> {
                sortPizzas(true)
                return true
            }
            R.id.mOrdenarZA -> {
                sortPizzas(false)
                return true
            }
            R.id.mConfiguracio -> {
                val intent = Intent(this, configuracion_IVA::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private suspend fun loadPizzasFromDatabase() {
        val pizzasFromDb = withContext(Dispatchers.IO) {
            database.pizzaDao().getAllPizzas()
        }
        pizzaList.clear()
        pizzaList.addAll(pizzasFromDb)
        adapter.notifyDataSetChanged()
    }

    private fun filterPizzas(type: String) {
        lifecycleScope.launch {
            val filteredPizzas = withContext(Dispatchers.IO) {
                database.pizzaDao().getPizzasByType(type)
            }
            pizzaList.clear()
            pizzaList.addAll(filteredPizzas)
            adapter.notifyDataSetChanged()
        }
    }

    private fun sortPizzas(ascending: Boolean) {
        pizzaList.sortBy { it.description }
        if (!ascending) {
            pizzaList.reverse()
        }
        adapter.notifyDataSetChanged()
    }

    private fun sortPizzasByDescription() {
        pizzaList.sortBy { it.description }
        adapter.notifyDataSetChanged()
    }
}
