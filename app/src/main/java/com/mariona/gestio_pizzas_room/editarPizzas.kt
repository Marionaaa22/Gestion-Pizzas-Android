package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.mariona.gestio_pizzas_room.MainActivity.Companion.EDIT_PIZZA_REQUEST_CODE
import com.mariona.gestio_pizzas_room.room.Pizzas
import com.mariona.gestio_pizzas_room.room.PizzasDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import com.google.android.material.snackbar.Snackbar

class editarPizzas : AppCompatActivity() {
    private lateinit var pizzaDao: PizzasDao
    private lateinit var sharedPreferences: SharedPreferences
    private val database by lazy {
        Room.databaseBuilder(applicationContext, AppDB::class.java, "pizza-database").build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_pizzas)

        pizzaDao = database.pizzaDao()
        sharedPreferences = getSharedPreferences("PizzaPreferences", Context.MODE_PRIVATE)

        val pizza = intent.getSerializableExtra("PIZZA") as? Pizzas

        val tvReferencia = findViewById<TextView>(R.id.tv_reference)
        val etDescripcio = findViewById<EditText>(R.id.editarDescripcion)
        val etPreu = findViewById<EditText>(R.id.editarIva)
        val spinnerTipo = findViewById<Spinner>(R.id.spinnerTipo)
        val etReferencia = findViewById<EditText>(R.id.editarReferencia)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarEditar)

        pizza?.let {
            tvReferencia.text = it.referencia
            etDescripcio.setText(it.descripcion)
            etPreu.setText(it.precio.toString())
            etReferencia.setText(it.referencia)

            // Set up the spinner
            val adapter = ArrayAdapter.createFromResource(
                this,
                R.array.pizza_types, // Assuming you have an array of pizza types in res/values/strings.xml
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTipo.adapter = adapter

            // Set the spinner to the current type
            val position = adapter.getPosition(it.tipo)
            spinnerTipo.setSelection(position)
        }

        btnGuardar.setOnClickListener {
            val novaDescripcio = etDescripcio.text.toString()
            val nouPreu = etPreu.text.toString().toDoubleOrNull()
            val nouReferencia = etReferencia.text.toString()
            val nouTipo = spinnerTipo.selectedItem.toString()

            if (novaDescripcio.isBlank() || nouPreu == null || nouReferencia.isBlank()) {
                Snackbar.make(it, "Por favor, completa todos los campos", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefix = when (nouTipo) {
                "PIZZA" -> "PI"
                "PIZZA VEGANA" -> "PV"
                "PIZZA CELIACA" -> "PC"
                "TOPPING" -> "TO"
                else -> null
            }

            if (!nouReferencia.startsWith(prefix ?: "")) {
                Snackbar.make(it, "La referencia debe comenzar con $prefix", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tipusIva = sharedPreferences.getFloat("tipusIva", 21f)
            val nouPreuIVA = nouPreu * (1 + tipusIva / 100)

            CoroutineScope(Dispatchers.IO).launch {
                val pizzaExistente = pizzaDao.getPizzaByReference(nouReferencia)
                if (pizzaExistente != null && pizzaExistente.referencia != pizza?.referencia) {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(btnGuardar, "La referencia ya existe", Snackbar.LENGTH_SHORT).show()
                    }
                    return@launch //
                }

                val pizzaActualitzada = Pizzas(
                    referencia = nouReferencia,
                    descripcion = novaDescripcio,
                    tipo = nouTipo,
                    precio = nouPreu,
                    precioIVA = nouPreuIVA
                )
                pizzaDao.updatePizza(pizzaActualitzada)

                withContext(Dispatchers.Main) {
                    val resultIntent = Intent().apply {
                        putExtra("UPDATED_PIZZA", pizzaActualitzada)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }
}