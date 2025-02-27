package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.mariona.gestio_pizzas_room.room.PizzasDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class configuracion_IVA : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var pizzaDao: PizzasDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_iva)

        // Inicializar la base de datos y obtener el dao
        val database = Room.databaseBuilder(applicationContext, AppDB::class.java, "pizza-database").build()
        pizzaDao = database.pizzaDao()
        sharedPreferences = getSharedPreferences("PizzaPreferences", Context.MODE_PRIVATE)

        // Obtener el valor actual del IVA (predeterminado al 21%)
        val currentTax = sharedPreferences.getFloat("taxRate", 21f)
        findViewById<TextView>(R.id.tvIvaActual).text = "IVA actual: $currentTax%"

        val etNewTax = findViewById<EditText>(R.id.nouIva)
        val btnSave = findViewById<Button>(R.id.btnGuardarIva)

        btnSave.setOnClickListener {
            // Obtener el nuevo valor de IVA del usuario
            val newTax = etNewTax.text.toString().toFloatOrNull()

            if (newTax != null && newTax > 0) {
                // Guardar el nuevo valor del IVA en las SharedPreferences
                sharedPreferences.edit().putFloat("taxRate", newTax).apply()

                // Lanzar la actualización de las pizzas en un hilo de trabajo
                CoroutineScope(Dispatchers.IO).launch {
                    val pizzas = pizzaDao.getAllPizzas()

                    pizzas.forEach { pizza ->
                        val priceWithTax = pizza.precio * (1 + newTax / 100)
                        // Log para verificar si el cálculo del precio es correcto
                        android.util.Log.d("configuracion_IVA", "Actualizando pizza: ${pizza.referencia}, precio con IVA: $priceWithTax")
                        // Actualizar el precio con IVA de cada pizza en la base de datos
                        pizzaDao.updatePizza(pizza.copy(precioIVA = priceWithTax))
                    }

                    // Notificar que se han actualizado correctamente las pizzas
                    withContext(Dispatchers.Main) {
                        val resultIntent = Intent().apply {
                            putExtra("NEW_TAX", newTax)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish() // Cerrar la actividad
                    }
                }
            } else {
                runOnUiThread {
                    // Si el IVA no es válido, mostrar un mensaje de error
                    Toast.makeText(this, "Por favor, ingrese un valor válido para el IVA", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
