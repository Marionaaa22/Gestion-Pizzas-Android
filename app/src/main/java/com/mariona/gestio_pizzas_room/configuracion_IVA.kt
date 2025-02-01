package com.mariona.gestio_pizzas_room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.mariona.gestio_pizzas_room.room.Pizzas
import com.mariona.gestio_pizzas_room.room.PizzasDao
import com.mariona.gestio_pizzas_room.room.PizzasDataBase
import kotlinx.coroutines.launch

class configuracion_IVA : AppCompatActivity() {

    private lateinit var ivaEditText: EditText
    private lateinit var saveButton: android.widget.Button
    private lateinit var pizzaDao: PizzasDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_iva)

        ivaEditText = findViewById(R.id.inputIVA)
        saveButton = findViewById(R.id.btnGuardarIVA)

        // Inicializamos la base de datos
        val db = PizzasDataBase.getDatabase(this, lifecycleScope)
        pizzaDao = db.pizzasDao()

        // Configurar el botón de guardar
        saveButton.setOnClickListener {
            val ivaValue = ivaEditText.text.toString().toFloatOrNull()

            if (ivaValue != null) {
                // Guardamos el nuevo valor de IVA en la base de datos
                lifecycleScope.launch {
                    pizzaDao.getIva()

                    // Confirmamos que se guardó el IVA
                    Snackbar.make(it, "IVA guardado correctamente", Snackbar.LENGTH_SHORT)
                        .show()

                    // Notificamos a la actividad principal que el IVA ha sido actualizado
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } else {
                Toast.makeText(this, "Por favor ingrese un valor válido para el IVA", Toast.LENGTH_SHORT).show()
            }
        }
    }
}