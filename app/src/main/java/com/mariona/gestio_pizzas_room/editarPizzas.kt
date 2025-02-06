package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

        val reference = intent.getStringExtra("REFERENCIA")
        val pizza = intent.getSerializableExtra("PIZZA") as? Pizzas

        val tvReferencia = findViewById<TextView>(R.id.tv_reference)
        val etDescripcio = findViewById<EditText>(R.id.editarDescripcion)
        val etPreu = findViewById<EditText>(R.id.editarIva)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarEditar)

        pizza?.let {
            tvReferencia.text = it.referencia
            etDescripcio.setText(it.descripcion)
            etPreu.setText(it.precio.toString())

            btnGuardar.setOnClickListener {
                val novaDescripcio = etDescripcio.text.toString()
                val nouPreu = etPreu.text.toString().toDoubleOrNull()

                if (nouPreu != null) {
                    val tipusIva = sharedPreferences.getFloat("tipusIva", 21f)
                    val nouPreuIVA = nouPreu * (1 + tipusIva / 100)

                    CoroutineScope(Dispatchers.IO).launch {
                        val pizzaActualitzada = Pizzas(
                            referencia = pizza.referencia,
                            descripcion = novaDescripcio,
                            tipo = pizza.tipo,
                            precio = nouPreu,
                            precioIVA = nouPreuIVA
                        )
                        pizzaDao.updatePizza(pizzaActualitzada)

                        withContext(Dispatchers.Main) {
                            val resultIntent = Intent().apply {
                                putExtra("UPDATED_PIZZA", pizzaActualitzada as Serializable)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    }
                } else {
                    etPreu.error = "Introduce un valor válido para el precio"
                }
            }
        }
    }

    private fun editPizza(pizza: Pizzas) {
        Log.d("MainActivity", "Editando pizza: ${pizza.referencia}")
        val intent = Intent(this, editarPizzas::class.java).apply {
            putExtra("PIZZA", pizza)
        }
        startActivityForResult(intent, EDIT_PIZZA_REQUEST_CODE)
    }
}