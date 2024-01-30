package org.unizd.rma.peric.pokedex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PokemonAdapter(val list: List<String>,
                     val itemClickListener : OnItemClickListener)
    : RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(pokemon : String)
    }
    inner class ViewHolder(itemView : View):
        RecyclerView.ViewHolder(itemView){
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pokemon_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = list[position]
        holder.tvName.text = pokemon

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(pokemon)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
