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

class configuracion_IVA : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var pizzaDao: PizzasDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_iva)

        val database = Room.databaseBuilder(applicationContext, AppDB::class.java, "pizza-database").build()
        pizzaDao = database.pizzaDao()
        sharedPreferences = getSharedPreferences("PizzaPreferences", Context.MODE_PRIVATE)

        val currentTax = sharedPreferences.getFloat("taxRate", 21f) // IVA predeterminado al 21%
        findViewById<TextView>(R.id.tvIvaActual).text = "IVA actual: $currentTax%"

        val etNewTax = findViewById<EditText>(R.id.nouIva)
        val btnSave = findViewById<Button>(R.id.btnGuardarIva)

        btnSave.setOnClickListener {
            val newTax = etNewTax.text.toString().toFloatOrNull()

            if (newTax != null && newTax > 0) {
                sharedPreferences.edit().putFloat("taxRate", newTax).apply()

                CoroutineScope(Dispatchers.IO).launch {
                    val pizzas = pizzaDao.getAllPizzas()
                    pizzas.forEach { pizza ->
                        val priceWithTax = pizza.precio * (1 + newTax / 100)
                        pizzaDao.updatePizza(pizza.copy(precioIVA = priceWithTax))
                    }

                    val resultIntent = Intent().apply {
                        putExtra("NEW_TAX", newTax)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            } else {

                runOnUiThread {
                    Toast.makeText(this, "Por favor, ingrese un valor válido para el IVA", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }
}
