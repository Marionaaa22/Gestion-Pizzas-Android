package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.mariona.gestio_pizzas_room.room.Pizzas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class editarPizzas : AppCompatActivity() {
    private lateinit var pizzaDao: PizzasDao
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_pizzas)

        val database = Room.databaseBuilder(applicationContext, AppDB::class.java, "pizza-database").build()
        pizzaDao = database.pizzaDao()
        sharedPreferences = getSharedPreferences("PizzaPreferences", Context.MODE_PRIVATE)

        val reference = intent.getStringExtra("REFERENCE")
        val pizza = intent.getParcelableExtra<Pizzas>("PIZZA")

        val tvReference = findViewById<TextView>(R.id.tv_reference)
        val etDescription = findViewById<EditText>(R.id.editarDescripcion)
        val etPrice = findViewById<EditText>(R.id.editarIva)
        val btnSave = findViewById<Button>(R.id.btnGuardarEditar)

        pizza?.let {
            tvReference.text = "Referencia: ${it.reference}"
            etDescription.setText(it.description)
            etPrice.setText(it.priceWithoutTax.toString())

            btnSave.setOnClickListener {
                val newDescription = etDescription.text.toString()
                val newPriceWithoutTax = etPrice.text.toString().toDoubleOrNull()

                if (newPriceWithoutTax != null) {
                    val taxRate = sharedPreferences.getFloat("taxRate", 21f)
                    val newPriceWithTax = newPriceWithoutTax * (1 + taxRate / 100)

                    CoroutineScope(Dispatchers.IO).launch {
                        val updatedPizza = Pizzas(
                            reference = it.reference,
                            description = newDescription,
                            type = it.type,
                            priceWithoutTax = newPriceWithoutTax,
                            priceWithTax = newPriceWithTax
                        )
                        pizzaDao.updatePizza(updatedPizza)

                        // Pass the result back to the main activity
                        val resultIntent = Intent().apply {
                            putExtra("UPDATED_PIZZA", updatedPizza)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
        }
    }
}