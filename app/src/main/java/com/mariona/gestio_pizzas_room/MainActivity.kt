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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mariona.gestio_pizzas_room.adapter.pizzaAdapter
import com.mariona.gestio_pizzas_room.room.PizzasDao
import com.mariona.gestio_pizzas_room.room.PizzasDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var pizzaRecyclerView: RecyclerView
    lateinit var pizzaDao: PizzasDao
    lateinit var pizzaAdapter: pizzaAdapter

    // Registro para manejar el resultado de la configuración del IVA
    val configIvaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Cuando el IVA se actualiza, recargamos las pizzas
            cargarPizzas()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuración del Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Inicialización de la base de datos y DAO
        val db = PizzasDataBase.getDatabase(this, lifecycleScope)
        pizzaDao = db.pizzasDao()

        // Configuración del RecyclerView y su adaptador
        pizzaRecyclerView = findViewById(R.id.rvDatos)
        pizzaAdapter = pizzaAdapter(this)
        pizzaRecyclerView.adapter = pizzaAdapter

        // Cargar las pizzas cuando la actividad se crea
        cargarPizzas()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mAfegirPizza -> {
                // Agregar una nueva pizza
                val intent = Intent(this, addPizza::class.java)
                startActivity(intent)
                true
            }
            R.id.mConfiguracio -> {
                // Configuración del IVA
                val intent = Intent(this, configuracion_IVA::class.java)
                configIvaLauncher.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPizzas() // Recargamos las pizzas cuando la actividad se reanuda
    }

    // Función para cargar las pizzas en el RecyclerView
    private fun cargarPizzas() {
        lifecycleScope.launch(Dispatchers.IO) {
            val pizzas = pizzaDao.getPizzes()
            withContext(Dispatchers.Main) {
                if (pizzas.isNullOrEmpty()) {
                    Snackbar.make(
                        findViewById(R.id.main),
                        "No hay pizzas disponibles",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    pizzaAdapter.pizzes = pizzas.toMutableList()
                    pizzaAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
