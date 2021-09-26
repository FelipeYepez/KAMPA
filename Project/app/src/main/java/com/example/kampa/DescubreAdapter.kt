package com.example.kampa

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kampa.models.Publicacion


class DescubreAdapter(private val context: Context, private val data: Publicacion): RecyclerView.Adapter<DescubreAdapter.CardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescubreAdapter.CardViewHolder {
        var layoutInflater =  LayoutInflater.from(parent.context)
        return CardViewHolder(layoutInflater.inflate(R.layout.swipe_card_layout, parent, false))
    }

    override fun getItemCount(): Int {
        //return data.size;
        return 1;
    }

    override fun onBindViewHolder(holder: DescubreAdapter.CardViewHolder, position: Int) {
        //var item: Publicacion = data[position]
        //holder.bind(item, context)
        holder.bind(data, context)
    }

    class CardViewHolder(view: View): RecyclerView.ViewHolder(view){
        private var swipeCardImage: ImageView = view.findViewById(R.id.swipeCardImage)
        private var swipeCardName: TextView = view.findViewById(R.id.swipeCardName)
        private var swipeCardDescription: TextView = view.findViewById(R.id.swipeCardDescription)

        fun bind(item: Publicacion, context: Context){
            swipeCardImage.setImageResource(R.drawable.esencia_patrimonio)
            swipeCardName.setText(item.idSitio.toString())
            swipeCardDescription.setText(item.descripcion.toString())
        }
    }
}