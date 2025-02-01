package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mariona.gestio_pizzas_room.room.PizzasDao
import com.mariona.gestio_pizzas_room.room.PizzasDataBase
import kotlinx.coroutines.launch

class configuracion_IVA : AppCompatActivity() {

    private lateinit var ivaEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var pizzaDao: PizzasDao
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_iva)

        ivaEditText = findViewById(R.id.inputIVA)
        saveButton = findViewById(R.id.btnGuardarIVA)

        // Inicializamos la base de datos y SharedPreferences
        val db = PizzasDataBase.getDatabase(this, lifecycleScope)
        pizzaDao = db.pizzasDao()
        sharedPreferences = getSharedPreferences("PizzaPreferences", MODE_PRIVATE)

        // Cargar el IVA almacenado en SharedPreferences (valor predeterminado: 21%)
        val currentTax = sharedPreferences.getFloat("taxRate", 21f)
        ivaEditText.setText(currentTax.toString())  // Establecemos el IVA actual en el EditText

        saveButton.setOnClickListener {
            val ivaValue = ivaEditText.text.toString().toFloatOrNull()

            if (ivaValue != null && ivaValue > 0) {
                // Guardamos el nuevo valor de IVA en SharedPreferences
                sharedPreferences.edit().putFloat("taxRate", ivaValue).apply()

                // Actualizamos el IVA en la base de datos para todas las pizzas
                lifecycleScope.launch {
                    val pizzas = pizzaDao.getPizzes()  // Obtener todas las pizzas
                    pizzas.forEach { pizza ->
                        // Calculamos el precio con el nuevo IVA
                        val priceWithTax = pizza.preuSenseIVA * (1 + ivaValue / 100)
                        pizzaDao.updatePizza(pizza.copy(preuAmbIVA = priceWithTax))  // Actualizar pizza
                    }

                    // Confirmar que el IVA se guardó correctamente
                    Snackbar.make(it, "IVA guardado correctamente", Snackbar.LENGTH_SHORT).show()

                    // Notificamos a la actividad principal que el IVA ha sido actualizado
                    setResult(Activity.RESULT_OK)
                    finish()  // Finalizar la actividad
                }
            } else {
                // Mostrar un mensaje de error si el IVA no es válido
                Toast.makeText(this, "Por favor ingrese un valor válido para el IVA", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
