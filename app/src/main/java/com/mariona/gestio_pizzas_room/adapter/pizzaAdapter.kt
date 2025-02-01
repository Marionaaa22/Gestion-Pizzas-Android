package com.mariona.gestio_pizzas_room.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mariona.gestio_pizzas_room.R
import com.mariona.gestio_pizzas_room.infoPizzas
import com.mariona.gestio_pizzas_room.room.Pizzas

class pizzaAdapter(private val context: Context, var pizzes: MutableList<Pizzas> = mutableListOf()) :
    RecyclerView.Adapter<pizzaAdapter.PizzaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PizzaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PizzaViewHolder(layoutInflater.inflate(R.layout.activity_info_pizzas, parent, false))
    }

    override fun onBindViewHolder(holder: PizzaViewHolder, position: Int) {
        val item = pizzes[position]
        holder.bind(item)
    }

    override fun getItemCount() = pizzes.size

    inner class PizzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val referencia: TextView = itemView.findViewById(R.id.tvReferencia)
        private val tipo: TextView = itemView.findViewById(R.id.tvTipusPizza)
        private val descripcion: TextView = itemView.findViewById(R.id.tvDescripcioPizza)
        private val precioSinIVA: TextView = itemView.findViewById(R.id.tvPreu)

        fun bind(pizza: Pizzas) {
            referencia.text = "Referencia: ${pizza.referencia}"
            tipo.text = "Tipo: ${pizza.tipos}"
            descripcion.text = "Descripción: ${pizza.despcripcion}"
            precioSinIVA.text = "Precio sin IVA: ${pizza.preuSenseIVA}"

            itemView.setOnClickListener {
                val intent = Intent(context, infoPizzas::class.java).apply {
                    putExtra("referencia", pizza.referencia)
                    putExtra("tipos", pizza.tipos)
                    putExtra("despcripcion", pizza.despcripcion)
                    putExtra("preuSenseIVA", pizza.preuSenseIVA)
                }
                context.startActivity(intent)
            }
        }
    }
}
