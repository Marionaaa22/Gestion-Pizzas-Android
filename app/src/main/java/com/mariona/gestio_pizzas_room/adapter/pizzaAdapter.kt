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
import com.google.android.material.snackbar.Snackbar
import com.mariona.gestio_pizzas_room.R
import com.mariona.gestio_pizzas_room.infoPizzas
import com.mariona.gestio_pizzas_room.room.Pizzas

class pizzaAdapter () : RecyclerView.Adapter<pizzaAdapter.PizzaViewHolder>() {

    var pizzes: MutableList<Pizzas> = ArrayList()
    lateinit var context: Context

    fun pizzaAdapter(pizzes: MutableList<Pizzas>, context: Context) {
        this.pizzes = pizzes
        this.context = context
    }

    override fun onBindViewHolder(holder: PizzaViewHolder, position: Int) {
        val item = pizzes.get(position)
        holder.bind(item, context)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PizzaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PizzaViewHolder(layoutInflater.inflate(R.layout.activity_info_pizzas, parent, false))
    }


    override fun getItemCount(): Int {
        return pizzes.size
    }

    inner class PizzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val referencia: TextView = itemView.findViewById(R.id.textVeiw)
        val tipo: TextView = itemView.findViewById(R.id.tvTipusPizza)
        val descripcion: TextView = itemView.findViewById(R.id.tvDescripcioPizza)
        val precioIVA: TextView = itemView.findViewById(R.id.tvPreu)
        val precioSinIVA: TextView = itemView.findViewById(R.id.tvPreu)
        val btnEliminar: Button = itemView.findViewById(R.id.imgEliminar)

        fun bind(pizza: Pizzas, context: Context) {
            referencia.text = "Referencia: ${pizza.referencia}"
            tipo.text = "Tipo: ${pizza.tipos}"
            descripcion.text = "Descripción: ${pizza.despcripcion}"
            //precioIVA.text = "Precio con IVA: ${pizza.preuIVA}"
            precioSinIVA.text = "Precio sin IVA: ${pizza.preuSenseIVA}"

            cardView.setOnClickListener {
                val intent = Intent(context, infoPizzas::class.java)
                intent.putExtra("referencia", pizza.referencia)
                intent.putExtra("tipos", pizza.tipos)
                intent.putExtra("despcripcion", pizza.despcripcion)
                intent.putExtra("preuSenseIVA", pizza.preuSenseIVA)
                //intent.putExtra("preuIVA", pizza.preuIVA)
                context.startActivity(intent)
            }
        }
    }

}