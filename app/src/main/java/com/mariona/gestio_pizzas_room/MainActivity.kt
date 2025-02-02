package com.mariona.gestio_pizzas_room

import android.app.Activity
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
import com.mariona.gestio_pizzas_room.room.Pizzas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: pizzaAdapter
    private val pizzaList = mutableListOf<Pizzas>()
    private lateinit var database: AppDB

    companion object {
        const val ADD_PIZZA_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the database
        database = Room.databaseBuilder(
            applicationContext,
            AppDB::class.java, "pizza-database"
        ).build()

        // Set up the toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.menu)
        setSupportActionBar(toolbar)

        // Initialize the adapter
        adapter = pizzaAdapter(pizzaList, onDelete = { pizza ->
            deletePizza(pizza)
        }, onEdit = { pizza ->
            editPizza(pizza)
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
                startActivityForResult(intent, ADD_PIZZA_REQUEST_CODE)
                return true
            }
            R.id.mFiltrar -> return true
            R.id.mPizzes -> {
                return true
            }
            R.id.mOrdenarAZ -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PIZZA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                loadPizzasFromDatabase()
            }
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

    private fun deletePizza(pizza: Pizzas) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                database.pizzaDao().deletePizza(pizza)
            }
            pizzaList.remove(pizza)
            adapter.notifyDataSetChanged()
        }
    }

    private fun editPizza(pizza: Pizzas) {
        val intent = Intent(this, editarPizzas::class.java).apply {
            putExtra("PIZZA", pizza)
        }
        startActivity(intent)
    }
}