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
        setContentView(R.layout.activity_info_pizzas)

        val referencia = intent.getLongExtra("referencia", 0)
        val tipos = intent.getStringExtra("tipos")
        val despcripcion = intent.getStringExtra("despcripcion")
        val preuSenseIVA = intent.getFloatExtra("preuSenseIVA", 0f)

        findViewById<TextView>(R.id.tvReferencia).text = "Referencia: $referencia"
        findViewById<TextView>(R.id.tvTipusPizza).text = "Tipo: $tipos"
        findViewById<TextView>(R.id.tvDescripcioPizza).text = "Descripción: $despcripcion"
        findViewById<TextView>(R.id.tvPreuSense).text = "Precio sin IVA: $preuSenseIVA"
    }
}