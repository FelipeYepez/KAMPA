package com.example.kampa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kampa.R
import com.example.kampa.models.Wishlist

class FavoritosAdapter(private val context: Context?, private val data: List<Wishlist>)
    : RecyclerView.Adapter<FavoritosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): FavoritosAdapter.ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return FavoritosAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_favoritos, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FavoritosAdapter.ViewHolder, position: Int) {
        var item: Wishlist = data[position]
        if (context != null) {
            holder.bind(item, context)
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var ivFotoWishlist: ImageView = view.findViewById(R.id.ivFotoSitio)
        private var tvFavoritoTitle: TextView = view.findViewById(R.id.tvFavoritoTitle)

        fun bind(item: Wishlist, context: Context) {
            tvFavoritoTitle.text = item.nombre

            Glide.with(context)
                .load(item.fotoWishlist)
                .placeholder(R.drawable.buildings)
                .error(R.drawable.buildings)
                .into(ivFotoWishlist)
        }
    }
}