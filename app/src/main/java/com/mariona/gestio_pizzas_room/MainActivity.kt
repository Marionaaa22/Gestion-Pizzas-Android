package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.mariona.gestio_pizzas_room.adapter.pizzaAdapter
import com.mariona.gestio_pizzas_room.room.PizzasDao
import com.mariona.gestio_pizzas_room.room.AppDatabase
import com.mariona.gestio_pizzas_room.room.Pizzas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: pizzaAdapter
    private val pizzaList = mutableListOf<Pizzas>()
    private lateinit var database: AppDatabase
    private lateinit var pizzaDao: PizzasDao

    companion object {
        const val REQUEST_CODE_ADD_PIZZA = 100
        const val REQUEST_CODE_EDIT_PIZZA = 101
        const val REQUEST_CODE_CHANGE_TAX = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar la base de datos Room
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "pizza-database"
        ).build()

        pizzaDao = database.pizzaDao()
        setSupportActionBar(findViewById(R.id.menu))

        // Inicializar el adaptador con la funcionalidad de eliminar y editar
        adapter = pizzaAdapter(
            pizzaList,
            onDelete = { pizzaToDelete ->
                CoroutineScope(Dispatchers.IO).launch {
                    pizzaDao.deletePizza(pizzaToDelete)
                    // Actualizar la lista después de eliminar
                    val updatedPizzaList = pizzaDao.getAllPizzas()
                    runOnUiThread {
                        pizzaList.clear()
                        pizzaList.addAll(updatedPizzaList)
                        adapter.notifyDataSetChanged()
                    }
                }
            },
            onEdit = { pizzaToEdit ->
                val intent = Intent(this, editarPizzas::class.java)
                intent.putExtra("PIZZA", pizzaToEdit)
                startActivityForResult(intent, REQUEST_CODE_EDIT_PIZZA)
            }
        )

        // Configurar el RecyclerView
        findViewById<RecyclerView>(R.id.recyclerview).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // Cargar las pizzas desde la base de datos Room
        CoroutineScope(Dispatchers.IO).launch {
            val pizzasFromDb = pizzaDao.getAllPizzas()
            pizzaList.addAll(pizzasFromDb)
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
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
                startActivityForResult(intent, REQUEST_CODE_ADD_PIZZA)
            }

            R.id.mFiltrar -> {
                pizzaList.sortBy { it.description }
                adapter.notifyDataSetChanged()
            }

            R.id.pi -> adapter.updateData(pizzaList.filter { it.type == "PI" })
            R.id.pc -> adapter.updateData(pizzaList.filter { it.type == "PC" })
            R.id.pv -> adapter.updateData(pizzaList.filter { it.type == "PV" })
            R.id.to -> adapter.updateData(pizzaList.filter { it.type == "TO" })
            R.id.configure_tax -> {
                val intent = Intent(this, configuracion_IVA::class.java)
                startActivityForResult(intent, REQUEST_CODE_CHANGE_TAX) // Usar código 200 para IVA
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CHANGE_TAX -> {
                    // Cambiar el IVA (requestCode 200)
                    val newTax = data?.getFloatExtra("NEW_TAX", 21f) ?: return

                    // Actualizar los precios con el nuevo IVA
                    CoroutineScope(Dispatchers.IO).launch {
                        val pizzas = pizzaDao.getAllPizzas()
                        pizzas.forEach { pizza ->
                            val updatedPriceWithTax = pizza.priceWithoutTax * (1 + newTax / 100)
                            pizzaDao.updatePizza(pizza.copy(priceWithTax = updatedPriceWithTax))
                        }

                        // Actualizar la UI después de cambiar el IVA
                        runOnUiThread {
                            pizzaList.clear()
                            pizzaList.addAll(pizzas)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                REQUEST_CODE_EDIT_PIZZA -> {
                    // Si es la edición de una pizza (requestCode 101)
                    val updatedPizza = data?.getParcelableExtra<Pizza>("UPDATED_PIZZA")

                    updatedPizza?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            pizzaDao.updatePizza(it)
                            val updatedPizzaList = pizzaDao.getAllPizzas()
                            runOnUiThread {
                                pizzaList.clear()
                                pizzaList.addAll(updatedPizzaList)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                REQUEST_CODE_ADD_PIZZA -> {
                    // Si es agregar una nueva pizza (requestCode 100)
                    val newPizza = data?.getParcelableExtra<Pizza>("NEW_PIZZA")

                    newPizza?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            pizzaDao.insertPizza(it)
                            val updatedPizzaList = pizzaDao.getAllPizzas()
                            runOnUiThread {
                                pizzaList.add(it)
                                adapter.notifyItemInserted(pizzaList.size - 1)
                                pizzaList.clear()
                                pizzaList.addAll(updatedPizzaList)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }
}
