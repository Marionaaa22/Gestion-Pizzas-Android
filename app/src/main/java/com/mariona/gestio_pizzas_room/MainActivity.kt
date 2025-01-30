package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mariona.gestio_pizzas_room.adapter.pizzaAdapter
import com.mariona.gestio_pizzas_room.room.PizzasDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.mariona.gestio_pizzas_room.room.PizzasDataBase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var pizzaRecyclerView: RecyclerView
    lateinit var pizzaDao: PizzasDao
    lateinit var pizzaAdapter: pizzaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajuste de las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Inicialización de la base de datos y DAO
        val db = PizzasDataBase.getDatabase(this, lifecycleScope)
        pizzaDao = db.pizzasDao()

        // Configuración del RecyclerView y su adaptador
        pizzaRecyclerView = findViewById(R.id.rvDatos) // Asegúrate de tener este RecyclerView en tu layout
        pizzaAdapter = pizzaAdapter()
        pizzaRecyclerView.adapter = pizzaAdapter

        // Comprobamos si el IVA está configurado
        lifecycleScope.launch {
            val ivaValue = pizzaDao.getIva() // Verifica si el IVA está configurado

            if (ivaValue == null) {
                // Si el IVA no está configurado, mostramos el Snackbar
                withContext(Dispatchers.Main) {
                    Snackbar.make(
                        findViewById(R.id.main), // Aquí puedes cambiar el view donde se mostrará el Snackbar
                        "Debe configurar el IVA antes de agregar una pizza",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Registrar el lanzador para la actividad de configuración IVA
        val configIvaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Cuando el IVA se actualiza, recargamos las pizzas
                carregarPizzes()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mAfegirPizza -> {
                // Verificamos si el IVA está configurado antes de permitir agregar una pizza
                lifecycleScope.launch {
                    val ivaValue = pizzaDao.getIva()

                    if (ivaValue == null) {
                        // Si no está configurado, mostramos un Snackbar
                        withContext(Dispatchers.Main) {
                            Snackbar.make(
                                findViewById(R.id.main),
                                "Debe configurar el IVA antes de agregar una pizza",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        // Si el IVA está configurado, seguimos adelante y permitimos agregar pizza
                        val intent = Intent(this@MainActivity, addPizza::class.java)
                        startActivity(intent)
                    }
                }
                true
            }

            R.id.mConfiguracio -> {
                val intent = Intent(this, configuracion_IVA::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        carregarPizzes() // Recargamos las pizzas cuando la actividad se reanuda
    }

    // Función para cargar las pizzas en el RecyclerView
    private fun carregarPizzes() {
        lifecycleScope.launch {
            val pizzas = withContext(Dispatchers.IO) {
                pizzaDao.getPizzes() // Obtener las pizzas desde la base de datos
            }
            pizzaAdapter.pizzes = pizzas.toMutableList()
            pizzaAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
        }
    }
}