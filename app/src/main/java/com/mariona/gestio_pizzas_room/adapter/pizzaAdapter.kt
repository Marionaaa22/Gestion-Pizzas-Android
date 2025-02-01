package com.mariona.gestio_pizzas_room.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mariona.gestio_pizzas_room.R
import com.mariona.gestio_pizzas_room.infoPizzas
import com.mariona.gestio_pizzas_room.room.Pizzas

class pizzaAdapter(
    private var pizzaList: MutableList<Pizza>,
    private val onDelete: (Pizza) -> Unit,
    private val onEdit: (Pizza) -> Unit // Nuevo callback
) : RecyclerView.Adapter<PizzaAdapter.PizzaViewHolder>() {

    class PizzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReferencia: TextView = itemView.findViewById(R.id.Referencia)
        val tvDescription: TextView = itemView.findViewById(R.id.Descripcio)
        val tvType: TextView = itemView.findViewById(R.id.Tipus)
        val tvPriceWithoutTax: TextView = itemView.findViewById(R.id.sesnseiva)
        val tvPriceWithTax: TextView = itemView.findViewById(R.id.iva)
        val btnDelete: ImageView = itemView.findViewById(R.id.Eliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PizzaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_row, parent, false)
        return PizzaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PizzaViewHolder, position: Int) {
        val pizza = pizzaList[position]

        holder.tvReferencia.text = pizza.reference
        holder.tvDescription.text = pizza.description
        holder.tvType.text = pizza.type
        holder.tvPriceWithoutTax.text = pizza.priceWithoutTax.toString()
        holder.tvPriceWithTax.text = pizza.priceWithTax.toString()
        holder.tvReferencia.setOnClickListener {
            onEdit(pizza) // Llamar al callback para editar
        }
        holder.btnDelete.setOnClickListener {
            val removedPizza = pizzaList[position]
            pizzaList.removeAt(position)
            notifyItemRemoved(position)
            onDelete(removedPizza) // Llama al callback para eliminarla de Room
        }
    }

    override fun getItemCount(): Int = pizzaList.size

    fun updateData(newList: List<Pizza>) {
        pizzaList.clear()
        pizzaList.addAll(newList)
        notifyDataSetChanged()
    }
}
