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
import com.mariona.gestio_pizzas_room.room.Pizzas
import com.mariona.gestio_pizzas_room.room.PizzasDao
import com.mariona.gestio_pizzas_room.room.PizzasDataBase
import kotlinx.coroutines.launch

class configuracion_IVA : AppCompatActivity() {

    private lateinit var ivaEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var pizzasDao: PizzasDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuracion_iva)

        //configurar la toolbar para ir para atras
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        ivaEditText = findViewById(R.id.inputIVA)
        saveButton = findViewById(R.id.btnGuardarIVA)

        // Inicializamos la base de datos
        val db = Room.databaseBuilder(
            applicationContext,
            PizzasDataBase::class.java, "pizzeria-database"
        ).build()

        pizzasDao = db.pizzasDao()

        // Cargar el IVA configurado desde la base de datos
        lifecycleScope.launch {
            val ivaValue = pizzasDao.getIva() // Asegúrate de que este método exista en el DAO
            ivaEditText.setText(ivaValue.toString()) // Muestra el IVA guardado en el EditText
        }

        // Guardar el valor de IVA
        saveButton.setOnClickListener {
            val ivaValue = ivaEditText.text.toString().toFloatOrNull()

            if (ivaValue != null) {
                // Guardamos el nuevo valor de IVA en la base de datos
                lifecycleScope.launch {
                    pizzasDao.updateIva(ivaValue) // Usamos el DAO adecuado para actualizar el IVA

                    // Confirmamos que se guardó el IVA
                    Toast.makeText(this@configuracion_IVA, "IVA guardado", Toast.LENGTH_SHORT).show()

                    // Notificar a MainActivity que el IVA ha sido actualizado
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } else {
                Toast.makeText(this, "Por favor ingrese un valor válido para el IVA", Toast.LENGTH_SHORT).show()
            }
        }
    }
}