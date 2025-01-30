package com.mariona.gestio_pizzas_room

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class infoPizzas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info_pizzas)

        // Obtener los datos pasados
        val referencia = intent.getStringExtra("referencia")
        val tipo = intent.getStringExtra("tipos")
        val descripcion = intent.getStringExtra("despcripcion")
        val preuSenseIVA = intent.getFloatExtra("preuSenseIVA", 0f)
        val preuIVA = intent.getFloatExtra("preuIVA", 0f)

        // Mostrar los datos en las vistas correspondientes
        findViewById<TextView>(R.id.tvReferencia).text = referencia
        findViewById<TextView>(R.id.tvTipusPizza).text = tipo
        findViewById<TextView>(R.id.tvDescripcioPizza).text = descripcion
        findViewById<TextView>(R.id.tvPreuSense).text = preuSenseIVA.toString()
        findViewById<TextView>(R.id.tvPreu).text = preuIVA.toString()

    }
}