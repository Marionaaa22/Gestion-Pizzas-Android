package com.mariona.gestio_pizzas_room

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mariona.gestio_pizzas_room.room.Pizzas
import com.mariona.gestio_pizzas_room.room.PizzasDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class addPizza : AppCompatActivity() {

    private lateinit var inputReferencia: EditText
    private lateinit var inputDescripcio: EditText
    private lateinit var inputPreu: EditText
    private lateinit var spinner: Spinner
    private lateinit var mainView: View
    private lateinit var database: PizzasDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_pizza)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        inputReferencia = findViewById(R.id.inputReferencia)
        inputDescripcio = findViewById(R.id.inputDescripcion)
        inputPreu = findViewById(R.id.inputPreu)
        mainView = findViewById(R.id.main)

        database = PizzasDataBase.getDatabase(this, CoroutineScope(Dispatchers.IO))

        spinner  = findViewById(R.id.inputTipo)
        ArrayAdapter.createFromResource(
            this,
            R.array.pizza_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            if (validarInputs()) {
                guardarPizza()
            }
        }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            clearInputs()
            finish()
        }

        mainView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(
                    left = systemBars.left,
                    top = systemBars.top,
                    right = systemBars.right,
                    bottom = systemBars.bottom
                )
                insets
            }
        } ?: run {
            android.util.Log.e("AddPizza", "El View con ID 'main' no se encontró en la vista.")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun validarInputs(): Boolean {
        val referencia = inputReferencia.text.toString().trim()
        val descripcio = inputDescripcio.text.toString().trim()
        val preuText = inputPreu.text.toString().trim()
        val tipus = spinner.selectedItem.toString().trim()

        if (referencia.isEmpty() || referencia.length < 6) {
            Snackbar.make(mainView, "La referència ha de tenir com a mínim 6 caràcters",
                Snackbar.LENGTH_SHORT)
                .show()
            return false
        }

        val prefix = referencia.substring(0, 2)
        val validPrefix = when (tipus) {
            "PIZZA" -> "PI"
            "PIZZA VEGANA" -> "PV"
            "PIZZA CELIACA" -> "PC"
            "TOPPING" -> "TO"
            else -> ""
        }

        if (prefix != validPrefix) {
            Snackbar.make(mainView, "La referència ha de començar per" +
                    " $validPrefix per al tipus seleccionat.", Snackbar.LENGTH_SHORT)
                .show()
            return false
        }

        if (descripcio.isEmpty()) {
            Snackbar.make(mainView, "La descripció és obligatòria",
                Snackbar.LENGTH_SHORT)
                .show()
            return false
        }

        if (preuText.isEmpty()) {
            Snackbar.make(mainView, "El preu és obligatori",
                Snackbar.LENGTH_SHORT)
                .show()
            return false
        }

        val preu = preuText.toFloatOrNull()
        if (preu == null || preu <= 0) {
            Snackbar.make(mainView, "El preu ha de ser un valor positiu",
                Snackbar.LENGTH_SHORT)
                .show()
            return false
        }

        return true
    }

    private fun guardarPizza() {
        val referencia = inputReferencia.text.toString().trim()
        val descripcio = inputDescripcio.text.toString().trim()
        val tipus = spinner.selectedItem.toString().trim()
        val preuSenseIVA = inputPreu.text.toString().toFloat()

        val novaPizza = Pizzas(
            referencia = referencia,
            despcripcion = descripcio,
            tipos = tipus,
            preuSenseIVA = preuSenseIVA,
            iva = 0.21f
        )

        lifecycleScope.launch {
            database.pizzasDao().insertPizza(novaPizza)
            Snackbar.make(mainView, "Pizza afegida correctament!",
                Snackbar.LENGTH_SHORT).show()
            clearInputs()
            finish()
        }
    }

    private fun clearInputs() {
        inputReferencia.text.clear()
        inputDescripcio.text.clear()
        inputPreu.text.clear()
        spinner.setSelection(0)
    }

}
