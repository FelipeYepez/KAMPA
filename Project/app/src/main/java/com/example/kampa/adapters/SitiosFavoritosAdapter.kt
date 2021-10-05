package com.example.kampa.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kampa.R
import com.example.kampa.models.Wishlist
import com.example.kampa.models.WishlistSitio
import com.parse.ParseObject

class SitiosFavoritosAdapter(private val context: Context?,
                             private var data: List<WishlistSitio>)
    : RecyclerView.Adapter<SitiosFavoritosAdapter.ViewHolder>() {

    private val TAG = "SitiosFavoritosAdapter"

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(
                R.layout.item_sitios_favoritos,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addAll(datos: List<WishlistSitio>) {
        data = datos
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: WishlistSitio = data[position]
        if (context != null) {
            holder.bind(item, context)
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private var ivFotoSitio: ImageView = view.findViewById(R.id.ivFotoSitio)
        private var tvSitioTitle: TextView = view.findViewById(R.id.tvSitioTitle)
        private var tvSitioDescription: TextView = view.findViewById(R.id.tvSitioDescription)
        private var ibDirections: ImageButton = view.findViewById(R.id.ibDirections)

        fun bind(item: WishlistSitio, context: Context) {
            tvSitioTitle.text = item.idSitio?.nombre
            tvSitioDescription.text = item.idSitio?.descripcion

            // TODO: Click Listener to ibDirections and to the item

            Glide.with(context)
                .load(item.idSitio?.foto?.url)
                .placeholder(R.drawable.buildings)
                .error(R.drawable.buildings)
                .into(ivFotoSitio)
        }
    }
}