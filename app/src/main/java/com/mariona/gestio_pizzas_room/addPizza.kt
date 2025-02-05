package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.mariona.gestio_pizzas_room.room.Pizzas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class addPizza : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pizza)

        val spinner = findViewById<Spinner>(R.id.spinnerTipo)
        val etDescription = findViewById<EditText>(R.id.inputDescripcio)
        val etPrice = findViewById<EditText>(R.id.inputPrecio)
        val etReference = findViewById<EditText>(R.id.inputReferencia)
        val btnSave = findViewById<Button>(R.id.btnGuardarPizza)

        btnSave.setOnClickListener {

            val type = spinner.selectedItem.toString()
            val description = etDescription.text.toString()
            val priceWithoutTax = etPrice.text.toString().toDoubleOrNull()
            val reference = etReference.text.toString()

            if (type.isBlank() || description.isBlank() || priceWithoutTax == null || reference.isBlank()) {
                Snackbar.make(it, "Por favor, completa todos los campos", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefix = when (type) {
                "PIZZA" -> "PI"
                "PIZZA VEGANA" -> "PV"
                "PIZZA CELIACA" -> "PC"
                "TOPPING" -> "TO"
                else -> null
            }

            if (!reference.startsWith(prefix ?: "")) {
                Snackbar.make(it, "La referencia debe comenzar con $prefix", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val priceWithTax = calculatePriceWithTax(priceWithoutTax)

            // Crear  Pizza
            val pizza = Pizzas(reference, description, type, priceWithoutTax, priceWithTax)

            CoroutineScope(Dispatchers.IO).launch {
                val database = Room.databaseBuilder(
                    applicationContext,
                    AppDB::class.java, "pizza-database"
                ).build()

                val existingPizza = database.pizzaDao().getPizzaByReference(reference)
                if (existingPizza != null) {
                    runOnUiThread {
                        Snackbar.make(btnSave, "La referencia ya existe", Snackbar.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                database.pizzaDao().insertPizza(pizza)
                runOnUiThread {
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }

    private fun calculatePriceWithTax(priceWithoutTax: Double): Double {
        val taxRate = 0.21
        return priceWithoutTax * (1 + taxRate)
    }
}