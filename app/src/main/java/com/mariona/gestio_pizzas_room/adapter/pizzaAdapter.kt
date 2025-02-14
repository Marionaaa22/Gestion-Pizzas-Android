package com.mariona.gestio_pizzas_room.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mariona.gestio_pizzas_room.R
import com.mariona.gestio_pizzas_room.room.Pizzas

class pizzaAdapter(
    private var pizzaList: MutableList<Pizzas>,
    private val onDelete: (Pizzas) -> Unit,
    private val onEdit: (Pizzas) -> Unit
) : RecyclerView.Adapter<pizzaAdapter.PizzaViewHolder>() {

    class PizzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReferencia: TextView = itemView.findViewById(R.id.Referencia)
        val tvDescription: TextView = itemView.findViewById(R.id.Descripcio)
        val tvType: TextView = itemView.findViewById(R.id.Tipus)
        val tvPriceWithoutTax: TextView = itemView.findViewById(R.id.senseIva)
        val tvPriceWithTax: TextView = itemView.findViewById(R.id.iva)
        val btnDelete: ImageView = itemView.findViewById(R.id.Eliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PizzaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_info_pizzas, parent, false)
        return PizzaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PizzaViewHolder, position: Int) {
        val pizza = pizzaList[position]

        holder.tvReferencia.text = pizza.referencia
        holder.tvDescription.text = pizza.descripcion
        holder.tvType.text = pizza.tipo
        holder.tvPriceWithoutTax.text = pizza.precio.toString()
        holder.tvPriceWithTax.text = pizza.precioIVA.toString()

        // Cambiar el fondo de la CardView según el tipo de pizza
        val cardView = holder.itemView.findViewById<CardView>(R.id.card)
        val context = holder.itemView.context
        val color = when (pizza.tipo) {
            "PIZZA" -> R.color.pizza_color
            "PIZZA VEGANA" -> R.color.vegan_pizza_color
            "PIZZA CELIACA" -> R.color.celiac_pizza_color
            "TOPPING" -> R.color.topping_color
            else -> android.R.color.white
        }
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, color))

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

    fun updatePizzas(newPizzasList: List<Pizzas>) {
        pizzaList.clear()
        pizzaList.addAll(newPizzasList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = pizzaList.size

}
