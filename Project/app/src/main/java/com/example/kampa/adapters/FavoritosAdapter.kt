package com.example.kampa.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kampa.R
import com.example.kampa.adapters.FavoritosAdapter.ViewHolder
import com.example.kampa.interfaces.SitiosFavoritosInterface
import com.example.kampa.models.Wishlist

class FavoritosAdapter(private val context: Context?,
                       private val data: List<Wishlist>,
                       private val listener: SitiosFavoritosInterface)
    : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_favoritos, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Wishlist = data[position]
        if (context != null) {
            holder.bind(item, context)
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private var ivFotoWishlist: ImageView = view.findViewById(R.id.ivFotoSitio)
        private var tvFavoritoTitle: TextView = view.findViewById(R.id.tvFavoritoTitle)

        init {
            view.setOnClickListener(this)
        }

        fun bind(item: Wishlist, context: Context) {
            tvFavoritoTitle.text = item.nombre

            Glide.with(context)
                .load(item.fotoWishlist)
                .placeholder(R.drawable.buildings)
                .error(R.drawable.buildings)
                .into(ivFotoWishlist)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val wishlist = data[position]

            if (position != RecyclerView.NO_POSITION) {
                listener.passData(wishlist)
            }
        }
    }
}