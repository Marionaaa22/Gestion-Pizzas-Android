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
    private val context: Context,
    var pizzaList: MutableList<Pizzas> = mutableListOf(),
    private val onDelete: (Pizzas) -> Unit,  // Callback para eliminar pizza
    private val onEdit: (Pizzas) -> Unit     // Callback para editar pizza
) : RecyclerView.Adapter<pizzaAdapter.PizzaViewHolder>() {

    // ViewHolder que contiene las vistas para cada pizza
    inner class PizzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReferencia: TextView = itemView.findViewById(R.id.tvReferencia)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescripcioPizza)
        val tvType: TextView = itemView.findViewById(R.id.tvTipusPizza)
        val tvPriceWithoutTax: TextView = itemView.findViewById(R.id.tvPreu)
        val btnDelete: ImageView = itemView.findViewById(R.id.imgEliminar)

        // Método para enlazar los datos de la pizza con las vistas
        fun bind(pizza: Pizzas) {
            tvReferencia.text = "Referencia: ${pizza.referencia}"
            tvDescription.text = "Descripción: ${pizza.despcripcion}"
            tvType.text = "Tipo: ${pizza.tipos}"
            tvPriceWithoutTax.text = "Precio sin IVA: ${pizza.preuSenseIVA}"

            // Al hacer clic en la referencia, se llama a la función de editar
            tvReferencia.setOnClickListener {
                onEdit(pizza)  // Llamar al callback de edición
            }

            // Al hacer clic en el botón de eliminar, se elimina la pizza
            btnDelete.setOnClickListener {
                val removedPizza = pizzaList[adapterPosition] // Obtener la pizza eliminada
                pizzaList.removeAt(adapterPosition) // Eliminarla de la lista
                notifyItemRemoved(adapterPosition) // Notificar al adaptador que el ítem fue eliminado
                onDelete(removedPizza) // Llamar al callback de eliminación
            }
        }
    }

    // Método para actualizar la lista de pizzas
    fun setPizzas(pizzas: List<Pizzas>) {
        this.pizzaList = pizzas.toMutableList() // Actualizamos la lista de pizzas
        notifyDataSetChanged() // Notificamos al RecyclerView que los datos han cambiado
    }

    // Crear el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PizzaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_info_pizzas, parent, false)
        return PizzaViewHolder(view)
    }

    // Enlazar los datos de la pizza con las vistas
    override fun onBindViewHolder(holder: PizzaViewHolder, position: Int) {
        val pizza = pizzaList[position]
        holder.bind(pizza)
    }

    // Obtener el tamaño de la lista de pizzas
    override fun getItemCount(): Int = pizzaList.size

    // Función para actualizar la lista de pizzas
    fun updateData(newList: List<Pizzas>) {
        pizzaList.clear()
        pizzaList.addAll(newList)
        notifyDataSetChanged()
    }
}
