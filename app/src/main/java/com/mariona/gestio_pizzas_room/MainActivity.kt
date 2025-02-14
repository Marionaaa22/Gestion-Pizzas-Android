package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
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
        const val EDIT_PIZZA_REQUEST_CODE = 2
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
            R.id.mPizzes -> {
                filterPizzasByType("PIZZA")
                return true
            }
            R.id.mToppings -> {
                filterPizzasByType("TOPPING")
                return true
            }
            R.id.mPizzesCeliacas -> {
                filterPizzasByType("PIZZA CELIACA")
                return true
            }
            R.id.mPizzesVeganes -> {
                filterPizzasByType("PIZZA VEGANA")
                return true
            }

            R.id.mTodasPizzas -> {
                lifecycleScope.launch {
                    loadPizzasFromDatabase()
                }
                return true
            }

            R.id.mDescripcio -> {
                showSearchByDescriptionDialog()
                return true
            }
            R.id.mOrdenarAZ -> {
                pizzaList.sortBy { it.referencia }
                adapter.notifyDataSetChanged()
                return true
            }

            R.id.mOrdenarZA -> {
                pizzaList.sortBy { it.precio }
                adapter.notifyDataSetChanged()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_PIZZA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                loadPizzasFromDatabase()  // Recargar la lista después de agregar
            }
        }

        if (requestCode == EDIT_PIZZA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val updatedPizza = data?.getSerializableExtra("UPDATED_PIZZA") as? Pizzas
            updatedPizza?.let { updatedPizzaItem ->
                pizzaList.indexOfFirst { it.referencia == updatedPizzaItem.referencia }
                    .takeIf { it >= 0 }?.let { index ->
                        pizzaList[index] = updatedPizzaItem
                        adapter.updatePizzas(pizzaList)
                    }
            }
        }
    }

    private suspend fun loadPizzasFromDatabase() {
        val pizzasFromDb = withContext(Dispatchers.IO) {
            database.pizzaDao().getAllPizzas()
        }
        pizzaList.clear()
        pizzaList.addAll(pizzasFromDb)

        withContext(Dispatchers.Main) {
            adapter.notifyDataSetChanged()  // Notificar cambios en el hilo principal
        }
    }

    private fun filterPizzasByType(type: String) {
        lifecycleScope.launch {
            val filteredPizzas = withContext(Dispatchers.IO) {
                database.pizzaDao().getPizzasByType(type)
            }
            pizzaList.clear()
            pizzaList.addAll(filteredPizzas)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showSearchByDescriptionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Buscar por Descripción")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Buscar") { dialog, _ ->
            val descripcion = input.text.toString()
            filterPizzasByDescripcion(descripcion)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun filterPizzasByDescripcion(descripcion: String) {
        lifecycleScope.launch {
            val filteredPizzas = withContext(Dispatchers.IO) {
                database.pizzaDao().getPizzasByDescripcion("%$descripcion%")
            }
            pizzaList.clear()
            pizzaList.addAll(filteredPizzas)
            adapter.notifyDataSetChanged()
        }
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